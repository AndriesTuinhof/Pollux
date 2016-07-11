package betapanda.util.collision;
public class CollisionCollider {

	public void logic(){
		
	}
	
	public static void sphereCollideAABB(Sphere s, Box b){	//its the same as the box you can notice it on the edges
		if(s.getX() +s.getRadius() <b.getX())
			return;
		if(s.getX() -s.getRadius() >b.getX() +b.getWidth())
			return;

		if(s.getY() +s.getRadius() <b.getY())
			return;
		if(s.getY() -s.getRadius() >b.getY() +b.getHeight())
			return;

		if(s.getZ() +s.getRadius() <b.getZ())
			return;
		if(s.getZ() -s.getRadius() >b.getZ() +b.getDepth())
			return;
		
		if(s.p.mass.getLastx() <b.getX()){
			s.p.mass.setX(b.getX() -s.getRadius());
			s.p.mass.setXspeed(0);
		}
		else if(s.p.mass.getLastx() >b.getX() +b.getWidth()){
			s.p.mass.setX(b.getX() +b.getWidth() +s.getRadius());
			s.p.mass.setXspeed(0);
		}
		else if(s.p.mass.getLasty() <b.getY()){
			s.p.mass.setY(b.getY() -s.getRadius());
			s.p.mass.setYspeed(0);
		}
		else if(s.p.mass.getLasty() >b.getY() +b.getHeight()){
			s.p.mass.setY(b.getY() +b.getHeight() +s.getRadius());
			s.p.mass.setYspeed(0);
		}
		else if(s.p.mass.getLastz() <b.getZ()){
			s.p.mass.setZ(b.getZ() -s.getRadius());
			s.p.mass.setZspeed(0);
		}
		else if(s.p.mass.getLastz() >b.getZ() +b.getDepth()){
			s.p.mass.setZ(b.getZ() +b.getDepth() +s.getRadius());
			s.p.mass.setZspeed(0);
		}
	}
	
	public static void boxCollideBox(Box b1, Box b){
		if(b1.getX() +b1.getWidth() <b.getX())
			return;
		if(b1.getX() -b1.getWidth() >b.getX() +b.getWidth())
			return;

		if(b1.getY() +b1.getDepth() <b.getY())
			return;
		if(b1.getY() -b1.getDepth() >b.getY() +b.getDepth())
			return;

		if(b1.getZ() +b1.getHeight() <b.getZ())
			return;
		if(b1.getZ() -b1.getHeight() >b.getZ() +b.getHeight())
			return;
		
		if(b1.p.mass.getLastx() <b.getX())
			b1.p.mass.setX(b.getX() -b1.getWidth());
		else if(b1.p.mass.getLastx() >b.getX() +b.getWidth())
			b1.p.mass.setX(b.getX() +b.getWidth() +b1.getWidth());

		else if(b1.p.mass.getLasty() <b.getY())
			b1.p.mass.setY(b.getY() -b1.getDepth());
		else if(b1.p.mass.getLasty() >b.getY() +b.getDepth())
			b1.p.mass.setY(b.getY() +b.getDepth() +b1.getDepth());

		else if(b1.p.mass.getLastz() <b.getZ())
			b1.p.mass.setZ(b.getZ() -b1.getHeight());
		else if(b1.p.mass.getLastz() >b.getZ() +b.getHeight())
			b1.p.mass.setZ(b.getZ() +b.getHeight() +b1.getHeight());
	
	}
	
	public static void sphereCollideSphere(Sphere s1, Sphere s2){
		if(s1.getX() +s1.getRadius() <s2.getX())
			return;
		if(s1.getX() -s1.getRadius() >s2.getX() +s2.getRadius())
			return;

		if(s1.getY() +s1.getRadius() <s2.getY())
			return;
		if(s1.getY() -s1.getRadius() >s2.getY() +s2.getRadius())
			return;

		if(s1.getZ() +s1.getRadius() <s2.getZ())
			return;
		if(s1.getZ() -s1.getRadius() >s2.getZ() +s2.getRadius())
			return;
		
		if(s1.p.mass.getLastx() <s2.getX())
			s1.p.mass.setX(s2.getX() -s1.getRadius());
		else if(s1.p.mass.getLastx() >s2.getX() +s2.getRadius())
			s1.p.mass.setX(s2.getX() +s2.getRadius() +s1.getRadius());

		else if(s1.p.mass.getLasty() <s2.getY())
			s1.p.mass.setY(s2.getY() -s1.getRadius());
		else if(s1.p.mass.getLasty() >s2.getY() +s2.getRadius())
			s1.p.mass.setY(s2.getY() +s2.getRadius() +s1.getRadius());

		else if(s1.p.mass.getLastz() <s2.getZ())
			s1.p.mass.setZ(s2.getZ() -s1.getRadius());
		else if(s1.p.mass.getLastz() >s2.getZ() +s2.getRadius())
			s1.p.mass.setZ(s2.getZ() +s2.getRadius() +s1.getRadius());
//		float x =s1.getX() -s2.getX();
//		float y =s1.getY() -s2.getY();
//		float z =s1.getZ() -s2.getZ();
//		float d =(float) Math.sqrt(x *x +y *y +z *z);
//		if(d <s1.getRadius() +s2.getRadius())
//			return;
//		
//		Vector3f delta =new Vector3f();
//		delta.x =s2.getX() -s1.getX();
//		delta.y =s2.getY() -s1.getY();
//		delta.z =s2.getZ() -s1.getZ();
////		float de =(float) Math.sqrt(delta.x *delta.x +delta.y *delta.y +delta.z *delta.z);
////		float f =((s2.getRadius() +s1.getRadius()) -de) /de;
////		delta.x *=f;
////		delta.y *=f;
////		delta.z *=f;
//		
//		s1.p.mass.setX(s1.p.mass.getX() + delta.x);
//		s1.p.mass.setY(s1.p.mass.getY() + delta.y);
//		s1.p.mass.setZ(s1.p.mass.getZ() + delta.z);
	}
	
	public static vec3 a =new vec3(), s2 =new vec3(), c =new vec3();
	public static void sphereCollideTriangle(Sphere s1, vec3 v1, vec3 v2, vec3 v3){
		a.set(v2.x -v1.x, v2.y -v1.y, v2.z -v1.z);
		s2.set(v3.x -v1.x, v3.y -v1.y, v3.z -v1.z);

		c.x =(v1.y *v2.z) -(v2.y *v1.z);
		c.y =(v1.z *v2.x) -(v2.z *v1.x);
		c.z =(v1.x *v2.y) -(v2.x *v1.y); 
		
		float f =(float) (1f /Math.sqrt(c.x *c.x +c.y *c.y +c.z *c.z));
		c.x *=f;
		c.y *=f;
		c.z *=f;
	}
}
