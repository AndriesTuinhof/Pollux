package betapanda;

import org.lwjgl.openal.AL10;

import awesome.core.Input;
import awesome.core.PandaAudio;
import awesome.core.PandaDisplay;
import awesome.core.PandaEngine;
import awesome.core.Xbox;
import betapanda.core.Shader;
import betapanda.net.Network;
import betapanda.util.Keyboard;

class RedshawEngine
{
	static boolean running =true;
	
	public static void main(String[] args)
	{
		boolean demo = true;
		String path = "resources/";
		for(String s: args)
		{
			String[] ss = s.split("=");
			if("dev".equals(ss[0])) demo = false;
			if("path".equals(ss[0])) path = ss[1];
		}
		if(args.length>0 && args[0].equals("dev")) demo = false;
		Xbox.setup(1);
		Xbox.createController();
		
		if(demo)
		{
			PandaDisplay.enableFullscreen();
			PandaDisplay.setPreferredScreen(1);
		}
		PandaEngine.create("Pollux Game 2.0", path); // START UP ENGINE
		PandaAudio.create();
		if(demo) PandaDisplay.setMouseGrabbed(true);
		
		Network.open();
		
		PolluxGame game =new PolluxGame();
		if(!demo) AL10.alListenerf(AL10.AL_GAIN, 0.01f);
		game.initialize();
		
		while(running)
		{
			if(PandaDisplay.closePressed() || Input.keyReleased(Keyboard.KEY_ESCAPE)) running =false;
			game.logic();
			Shader.updateBufferBlocks();
			game.draw();
			
			PandaEngine.update();
			Xbox.update();
		}
		Xbox.dispose();
		Network.close();
		PandaEngine.destroy();
	}

}
