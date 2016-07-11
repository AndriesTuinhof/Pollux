package betapanda.util;

import java.util.ArrayList;

import awesome.gui.Component;

public class GUI_Screen extends Component{

	private float x, y, w, h;
	
	public ArrayList<Component> components;
	
	public GUI_Screen() {
		components =new ArrayList<>();
	}
	
	@Override
	public void logic() {
		for(Component c: components)if(c !=null){
			c.logic();
		}
		
	}

	@Override
	public void draw() {
		for(Component c: components)if(c !=null){
			c.draw();
		}
		
	}

	@Override
	public void update(float x, float y, float w, float h) {
		this.x =x;
		this.y =y;
		this.w =w;
		this.h =h;
		
		for(Component c: components)if(c !=null){
			c.update(x, y, w, h);
		}
		
	}
	
	public ArrayList<Component> getComponets(){
		return components;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public float getWidth() {
		return w;
	}

	@Override
	public float getHeight() {
		return h;
	}

	
}
