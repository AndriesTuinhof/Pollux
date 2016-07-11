package betapanda.gameplay.world;

import java.util.ArrayList;
import java.util.List;

import betapanda.gameplay.Entity;
import betapanda.util.collision.Box;

public class Octree {
	
	private int MAX_OBJECTS =10;
	private int MAX_LEVELS =5;
	
	private int level;
	private List<Entity> objects;
	private Octree[] nodes;
	
	private float x, y, w, h;
	
	public Octree(int level, float x, float y, float w, float h){
		objects =new ArrayList<Entity>();
		nodes =new Octree[4];
		this.x =x;
		this.y =y;
		this.w =w;
		this.h =h;
	}
	
	public Octree(int level, Chunk chunk){
		objects =new ArrayList<Entity>();
		nodes =new Octree[4];
		update();
	}

	public void clear(){
		objects.clear();
		
		for(int i =0; i <nodes.length; i++)if(nodes[i] !=null){
			nodes[i].clear();
			nodes[i] =null;
		}
	}

	public void update(){//maybe remove
		
	}
	
	private void split(){
		float w =this.w /2f;
		float h =this.h /2f;
		
		nodes[0] =new Octree(level +1, x +w, 	y, 		w, h);
		nodes[1] =new Octree(level +1, x, 		y, 		w, h);
		nodes[2] =new Octree(level +1, x, 		y +h, 	w, h);
		nodes[3] =new Octree(level +1, x +w, 	y +h, 	w, h);

	}
	
	private int getIndex(float x, float y){
		int index =-1;
		float vm =this.x +this.w /2f;
		float hm =this.y +this.h /2f;
		
		if(x >=vm) index |=2;
		if(y >=hm) index |=1;
		
		return index;
	}
	
	public void insert(Entity e){
		if(nodes[0] !=null){
			int i =getIndex(e.mass.getX(), e.mass.getY());
			
			if(i !=-1){
				nodes[i].insert(e);
				return;
			}
		}
		
		objects.add(e);
		
		if(objects.size() >MAX_OBJECTS && level <MAX_LEVELS){
			if(nodes[0] ==null)
				split();
		}
		
		int i =0;
		while(i <objects.size()){
			Entity object =objects.get(i);
			int index =getIndex(object.mass.getX(), object.mass.getY());
			if(index !=-1){
				nodes[index].insert(objects.remove(i));
			}
			else{
				i ++;
			}
		}
	}
	
	public List<Entity> retrieve(List<Entity> returnObjects, Box b){
		int index =getIndex(b.getX() +b.getWidth() /2f, b.getY() +b.getDepth() /2f);
		if(index !=-1 && nodes[0] !=null){
			nodes[index].retrieve(returnObjects, b);
		}
		
		returnObjects.addAll(objects);
		
		return returnObjects;
	}
	
	public List<Entity> retrieve(List<Entity> returnObjects){
		returnObjects.addAll(objects);
		
		return returnObjects;
	}
	
	public List<Entity> retrieve(List<Entity> returnObjects, float x, float y, float w, float h){
		int index =getIndex(x +w /2f, y +h /2f);
		if(index !=-1 && nodes[0] !=null){
			nodes[index].retrieve(returnObjects, x, y, w, h);
		}
		
		returnObjects.addAll(objects);
		
		return returnObjects;
	}
	
	public void draw(){
//		if(level ==MAX_LEVELS || nodes[0] !=null){
//			Painter3D.setColor(0.1f, 0.6f, 0.12f);
//			Painter3D.beginDrawing(true);
//			Painter3D.setVertex(x, y, 0.02f, 0, 0);
//			Painter3D.setVertex(x +w, y, 0.02f, 0, 0);
//			
//			Painter3D.setVertex(x, y, 0.02f, 0, 0);
//			Painter3D.setVertex(x, y +h, 0.02f, 0, 0);
//			
//			Painter3D.setVertex(x +w, y, 0.02f, 0, 0);
//			Painter3D.setVertex(x +w, y+h, 0.02f, 0, 0);
//			
//			Painter3D.setVertex(x, y+h, 0.02f, 0, 0);
//			Painter3D.setVertex(x +w, y+h, 0.02f, 0, 0);
//			
//			Painter3D.stopDrawing(GL11.GL_LINES);
//		}
//		else{
//			for(Octree o: nodes)if(o !=null)
//				o.draw();
//		}
	}
}
