package betapanda.core.collada;

import org.w3c.dom.Element;

public class Material
{
	public String id;
	public Effect instance_effect;
	public Material(Element e, Collada c)
	{
		id = e.getAttribute("id");
		Element ee = (Element) e.getElementsByTagName("instance_effect").item(0);
		instance_effect = c.effects.get(ee.getAttribute("url").substring(1));
	}

}
