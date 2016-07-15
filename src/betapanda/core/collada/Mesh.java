package betapanda.core.collada;


import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Mesh
{
	private HashMap<String, Source> sources;
	private HashMap<String, Element> pointers;
	public float[] renderedData;
	public String id;
	public Material material;
	
	public Mesh(Element item, Collada collada)
	{
		id = item.getAttribute("id");
		NodeList l = item.getElementsByTagName("mesh");
		if(l.getLength()!=1) throw new RuntimeException("Mesh: Geometry parent does not contain 1 mesh, actually has: "+l.getLength());
		Element mesh = (Element)l.item(0);
		// SOURCES
		NodeList srcList = mesh.getElementsByTagName("source");
		sources = new HashMap<>(srcList.getLength());
		for(int i=0;i<srcList.getLength();i++){
			Element e = (Element)srcList.item(i);
			sources.put(e.getAttribute("id"), new Source(e));
		}
		
		// VERTEX POINTER
		NodeList vertices = mesh.getElementsByTagName("vertices");
		if(vertices.getLength()!=1) throw new RuntimeException("Should have 1 vertices spec, found "+vertices.getLength());
		NodeList vertexInputs = ((Element)vertices.item(0)).getElementsByTagName("input");
		if(vertexInputs.getLength()!=1) throw new RuntimeException("Hmm!");
		String ptrPositions = ((Element)vertexInputs.item(0)).getAttribute("source").substring(1);
		
		// POLYLIST
		NodeList polyLists = mesh.getElementsByTagName("polylist");
		if(polyLists.getLength()!=1) throw new RuntimeException("Should have 1 polylist, found "+polyLists.getLength());
		Element polyList = (Element)polyLists.item(0);
		material = collada.materials.get(polyList.getAttribute("material"));
		System.out.println(material);
		int polygonCount = Integer.parseInt(polyList.getAttribute("count"));
		NodeList inputs = polyList.getElementsByTagName("input");
		// Pointers for vertex data
		pointers = new HashMap<String, Element>(inputs.getLength());
		for(int i=0;i<inputs.getLength();i++)
		{
			Element input = (Element) inputs.item(i);
			input.setAttribute("source", input.getAttribute("source").substring(1)); // Remove the # on references
			String semantic = input.getAttribute("semantic");
			pointers.put(semantic, input);
		}
		pointers.get("VERTEX").setAttribute("source", ptrPositions);
		Element vcounts = (Element)polyList.getElementsByTagName("vcount").item(0);
		String[] countParts = vcounts.getTextContent().trim().split(" ");
		String[] polyIndicesParts = polyList.getElementsByTagName("p").item(0).getTextContent().split(" ");
		int[] polyIndices = new int[polyIndicesParts.length];
		for(int i=0;i<polyIndicesParts.length;i++) polyIndices[i] = Integer.parseInt(polyIndicesParts[i]);
		
		Source ptrVertex=null, ptrNormal=null, ptrTexcoord=null;
		int[] offsets = new int[pointers.size()];
		if(pointers.get("VERTEX")!=null){
			ptrVertex = sources.get(pointers.get("VERTEX").getAttribute("source"));
			offsets[0] = Integer.parseInt(pointers.get("VERTEX").getAttribute("offset"));
		}
		if(pointers.get("NORMAL")!=null){
			ptrNormal = sources.get(pointers.get("NORMAL").getAttribute("source"));
			offsets[1] = Integer.parseInt(pointers.get("NORMAL").getAttribute("offset"));
		}
		if(pointers.get("TEXCOORD")!=null){
			ptrTexcoord = sources.get(pointers.get("TEXCOORD").getAttribute("source"));
			offsets[2] = Integer.parseInt(pointers.get("TEXCOORD").getAttribute("offset"));
		}
		
		float[] superData = new float[polygonCount*3*8];
		for(int p=0;p<polygonCount;p++)
		{
			int count = Integer.parseInt(countParts[p]);
			if(count!=3) throw new RuntimeException("This collada contains non triangles, with a "+count+" vertex polygon");
			for(int v = 0; v<3; v++){
				int i = p*24+v*8;
				if(ptrVertex!=null){
					superData[i+0] = ptrVertex.data[0 + 3*polyIndices[(p*3+v)*offsets.length+offsets[0]]];
					superData[i+1] = ptrVertex.data[1 + 3*polyIndices[(p*3+v)*offsets.length+offsets[0]]];
					superData[i+2] = ptrVertex.data[2 + 3*polyIndices[(p*3+v)*offsets.length+offsets[0]]];
				}
				
				if(ptrNormal!=null){
					superData[i+3] = ptrNormal.data[0 + 3*polyIndices[(p*3+v)*offsets.length+offsets[1]]];
					superData[i+4] = ptrNormal.data[1 + 3*polyIndices[(p*3+v)*offsets.length+offsets[1]]];
					superData[i+5] = ptrNormal.data[2 + 3*polyIndices[(p*3+v)*offsets.length+offsets[1]]];
				}
				
				if(ptrTexcoord!=null){
					superData[i+6] = ptrTexcoord.data[0 + 2*polyIndices[(p*3+v)*offsets.length+offsets[2]]];
					superData[i+7] = ptrTexcoord.data[1 + 2*polyIndices[(p*3+v)*offsets.length+offsets[2]]];
				}
			}
		}
		renderedData = superData;
	}

}
