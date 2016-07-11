package betapanda.core.collada;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Collada
{
	public HashMap<String, Mesh> meshes;
	public SceneNode[] nodes;
	
	public void loadScene() throws Exception
	{
		String path = "D:/Dropbox/Betapanda/PolluxProject/Resources/World/map_test.dae";
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
		Node n = doc.getDocumentElement().getFirstChild();
		while(n!=null)
		{
			if(n.getNodeName()=="library_geometries")
			{
				NodeList geometries = ((Element)n).getElementsByTagName("geometry");
				meshes = new HashMap<String, Mesh>(geometries.getLength());
				for(int i=0;i<geometries.getLength();i++)
				{
					Element e = (Element)geometries.item(i);
					meshes.put(e.getAttribute("id"), new Mesh(e));
				}
			}
			if(n.getNodeName()=="library_visual_scenes")
			{
				Element visual_scene = (Element)((Element)n).getElementsByTagName("visual_scene").item(0);
				NodeList nodeTags = visual_scene.getElementsByTagName("node");
				nodes = new SceneNode[nodeTags.getLength()];
				for(int i=0;i<nodeTags.getLength();i++) nodes[i] = new SceneNode((Element)nodeTags.item(i), meshes);
			}
			n = n.getNextSibling();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		new Collada().loadScene();
	}

}
