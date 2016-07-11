package betapanda.core.collada;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SceneNode
{
	public float[] matrix;
	public Mesh instanceGeometry;
	public String id;
	public SceneNode(Element item, HashMap<String, Mesh> meshes)
	{
		id = item.getAttribute("id");
		
		// Model Matrix
		NodeList nl = item.getElementsByTagName("matrix");
		if(nl.getLength()!=1) throw new RuntimeException("This node needs one matrix, "+nl.getLength()+" found");
		String[] matrixParts = nl.item(0).getTextContent().split(" ");
		matrix = new float[16];
		for(int i=0;i<16;i++) matrix[i] = Float.parseFloat(matrixParts[i]);

		// Geometry Instance
		Element e = (Element)item.getElementsByTagName("instance_geometry").item(0);
		if(e!=null) instanceGeometry = meshes.get(e.getAttribute("url").substring(1));
		System.out.println(instanceGeometry);
	}

}
