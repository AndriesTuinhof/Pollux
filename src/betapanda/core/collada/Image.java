package betapanda.core.collada;

import org.w3c.dom.Element;

public class Image {
	
	public String id;
	public String filename;
	public Image(Element e) {
		id = e.getAttribute("id");
		Element ee = (Element) e.getElementsByTagName("init_from").item(0);
		if(ee==null) throw new RuntimeException("There's no image supplied!");
		filename = ee.getTextContent();
	}

}
