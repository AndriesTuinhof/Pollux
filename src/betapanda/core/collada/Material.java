package betapanda.core.collada;

import org.w3c.dom.Element;

import awesome.core.ResourceLoader;

public class Material
{
	public String id;
	public Effect instance_effect;
	public int temp_texture;
	public Material(Element e, Collada c)
	{
		id = e.getAttribute("id");
		Element ee = (Element) e.getElementsByTagName("instance_effect").item(0);
		instance_effect = c.effects.get(ee.getAttribute("url").substring(1));
		temp_texture = ResourceLoader.getTexture("World/"+instance_effect.instance_image.filename, true);
	}

}
