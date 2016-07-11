package betapanda.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.lwjgl.opengl.GL20;

import awesome.core.ResourceLoader;

import static org.lwjgl.opengl.GL20.*;

public class Shader{ 
	
	private static Shader[] shaders =new Shader[40];
	private static BufferBlock[] bufferBlocks =new BufferBlock[10];
	
	public static Shader getShader(String name) {
		try	{
			String vert = name+".vert";
			String frag = name+".frag";
			int s =-1;
			for(int i =0; i <shaders.length; i++)if(shaders[i] !=null)
				if(shaders[i].name.equals(name))
					s =i;
			if(s ==-1)
				s =addShader(new Shader(name, vert, frag));
			return shaders[s];
		}
		catch(Exception e){
			System.out.println("[ENGINE]: Couldn't load shader!");
			e.printStackTrace();
			return null;
		}
	}
	
	public static Shader getShader(String name, String name1){
		try{
			Shader s =getShader(name);
			BufferBlock b;
			if((b =s.getBufferBlock()) !=null){
				if(b.getName().toUpperCase().equals(name1.toUpperCase()))
					return s;
				else{
					String vert = name+".vert";
					String frag = name+".frag";
					addShader(new Shader(name, vert, frag));
					s.bindShader(name1);
					return s;
				}
			}
			else{
				s.bindShader(name1);
				return s;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private static int addShader(Shader s){
		for(int i =0; i<shaders.length; i++)if(shaders[i] ==null){
			shaders[i] =s;
			return i;
		}
		return -1;
	}
	
	public static void addBufferBlock(BufferBlock b){
		for(int i =0; i <bufferBlocks.length; i++)if(bufferBlocks[i] ==null){
			bufferBlocks[i] =b;
			return;
		}
		
	}
	
	public void bindShader(String s){
		BufferBlock b =getBufferBlock(s);
		bufferBlock =b;
		b.bindBuffer(this);
	}
	
	public void bindShader(BufferBlock b){
		bufferBlock =b;
		b.bindBuffer(this);
	}
	
	private BufferBlock getBufferBlock(String n){
		for(int i =0; i <bufferBlocks.length; i++)if(bufferBlocks[i] !=null && bufferBlocks[i].getName().toUpperCase().equals(n.toUpperCase()))
			return bufferBlocks[i];
		
		System.out.println("Can't find "+n);
		return null;
	}
	
	public static void updateBufferBlocks(){
		for(BufferBlock b: bufferBlocks)if(b !=null){
			b.update();
		}
	}
	
	private BufferBlock bufferBlock;
	private int vertShader, fragShader, program;
	public String name;
	private Shader(String name, String vert, String frag) throws Exception{
		this.name =name;
		vertShader =loadShader(vert, GL_VERTEX_SHADER);
		fragShader =loadShader(frag, GL_FRAGMENT_SHADER);
		program =glCreateProgram();
		glAttachShader(program, vertShader);
		glAttachShader(program, fragShader);
		glLinkProgram(program);
		glValidateProgram(program);
	}
	
	private static int loadShader(String file, int type) throws Exception{
		InputStream is =ResourceLoader.openFile("Shaders/"+file);
//		System.out.println("I'm in "+Shader.class.getPackage());
//		InputStream is =Shader.class.getResourceAsStream("/betapanda/core/Shaders/"+file);
//		System.out.println(is);
		InputStreamReader isr =new InputStreamReader(is);
		BufferedReader reader =new BufferedReader(isr);
		
		String src ="";
		String line;
		
		while((line =reader.readLine()) !=null) 
			src +=line +"\n";
		
		reader.close();
		
		// Compiling
		int shader =glCreateShader(type);
		glShaderSource(shader, src);
		glCompileShader(shader);
		String err =glGetShaderInfoLog(shader, 500);
		if(err.length()>0) System.out.println("Shader ["+file+"] "+err);
		return shader;
	}

	public void bind(){
		GL20.glUseProgram(getProgram());
	}
	
	public int getProgram() {
		return program;
	}
	
	public BufferBlock getBufferBlock(){
		return bufferBlock;
	}

	
}
