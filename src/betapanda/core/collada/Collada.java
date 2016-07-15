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
	public HashMap<String, Image> images;
	public HashMap<String, Effect> effects;
	public HashMap<String, Material> materials;
	public SceneNode[] nodes;
	
	public void loadScene() throws Exception
	{
		String path = "D:/Dropbox/Betapanda/PolluxProject/Resources/World/map_test.dae";
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
		Node n = doc.getDocumentElement().getFirstChild();
		while(n!=null)
		{
			if(n.getNodeName()=="library_images")
			{
				NodeList imageNodes = ((Element)n).getElementsByTagName("image");
				images = new HashMap<String, Image>(imageNodes.getLength());
				for(int i=0;i<imageNodes.getLength();i++)
				{
					Element e = (Element)imageNodes.item(i);
					images.put(e.getAttribute("id"), new Image(e));
				}
			}
			if(n.getNodeName()=="library_effects")
			{
				NodeList effectNodes = ((Element)n).getElementsByTagName("effect");
				effects = new HashMap<String, Effect>(effectNodes.getLength());
				for(int i=0;i<effectNodes.getLength();i++)
				{
					Element e = (Element)effectNodes.item(i);
					effects.put(e.getAttribute("id"), new Effect(e, this));
				}
			}
			if(n.getNodeName()=="library_materials")
			{
				NodeList materialNodes = ((Element)n).getElementsByTagName("material");
				materials = new HashMap<String, Material>(materialNodes.getLength());
				for(int i=0;i<materialNodes.getLength();i++)
				{
					Element e = (Element)materialNodes.item(i);
					materials.put(e.getAttribute("id"), new Material(e, this));
				}
			}
			if(n.getNodeName()=="library_geometries")
			{
				NodeList geometries = ((Element)n).getElementsByTagName("geometry");
				meshes = new HashMap<String, Mesh>(geometries.getLength());
				for(int i=0;i<geometries.getLength();i++)
				{
					Element e = (Element)geometries.item(i);
					meshes.put(e.getAttribute("id"), new Mesh(e, this));
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
