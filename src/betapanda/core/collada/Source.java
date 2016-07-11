package betapanda.core.collada;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Source
{
	public float[] data;
	public String id;
	public Source(Element s)
	{
		id = s.getAttribute("id");
		NodeList l = s.getElementsByTagName("float_array");
		if(l.getLength()!=1) throw new RuntimeException("This source is not a float array!");
		Element floats = (Element)l.item(0);
		int count = Integer.parseInt(floats.getAttribute("count"));
		data = new float[count];
		String[] raw = floats.getTextContent().trim().split(" ");
		for(int i=0;i<count;i++) data[i] = Float.parseFloat(raw[i]);
	}
}
