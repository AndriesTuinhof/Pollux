package betapanda.core.collada;

import org.w3c.dom.Element;

public class Effect
{
	public String id;
	public Image instance_image;
	public Effect(Element e, Collada c)
	{
		id = e.getAttribute("id");
		Element ee = (Element) e.getElementsByTagName("surface").item(0);
		if(ee==null) throw new RuntimeException("Surface problem");
		String surfaceID = ee.getTextContent().trim();
		instance_image = c.images.get(surfaceID);
	}

}
