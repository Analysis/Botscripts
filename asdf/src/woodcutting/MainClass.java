package woodcutting;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.script.Category;

import static woodcutting.GEPrice.*;

@ScriptManifest(author = "Zandpaad", name = "Woodcutting", version = 1.4, description = "Woodcutting Script", category = Category.WOODCUTTING)

public class MainClass extends AbstractScript {
	
	private Area draynorBankArea = new Area(3093, 3241, 3096, 3245, 0);
	private Area lumbridgeBankArea = new Area(3200, 3198, 3246, 3238, 0);
	private Area varrockBankArea = new Area(3180, 9828, 3188, 9836, 0);
	private Area edgevilleBankArea = new Area(3091, 3488, 3094, 3494, 0);
	private Area grandexchangeBankArea = new Area(3173, 3486, 3169, 3493, 0);
	
	private Area lumbridgeTreeArea = new Area(3092, 3240, 3097, 3246, 0);
	private Area draynorTreeArea = new Area(3091, 3290, 3072, 3310, 0);
	private Area varrockTreeArea = new Area(3201, 3506, 3224, 3501, 0);
	private Area edgevilleTreeArea = new Area(3091, 3468, 3085, 3481, 0);
	private Area grandexchangeTreeArea = new Area(3208, 3504, 3222, 3500, 0);
	
	private Area draynorWillowArea = new Area(3096, 3223, 3079, 3241, 0);
	private Area lumbridgeWillowArea = new Area(3161, 3278, 3182, 3264, 0);
	
	private boolean startScript;
	private GUI gui;
	private BufferedImage img;
	
	private int logid;
	private int logsgained;
	private double logsxp;
	private int logprice;
	
	ArrayList<Area> areasTrees = new ArrayList<Area>();
	ArrayList<Area> areasBanks = new ArrayList<Area>();
	
	public void onStart() {
		gui = new GUI(this);
		gui.setVisible(true);	
		try {
			img = ImageIO.read(getClass().getResourceAsStream("/bbb.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		areasTrees.add(draynorTreeArea);
		areasTrees.add(lumbridgeTreeArea);
		areasTrees.add(varrockTreeArea);
		areasTrees.add(edgevilleTreeArea);
		areasTrees.add(grandexchangeTreeArea);
		
		areasBanks.add(draynorBankArea);
		areasBanks.add(lumbridgeBankArea);
		areasBanks.add(varrockBankArea);
		areasBanks.add(edgevilleBankArea);
		areasBanks.add(grandexchangeBankArea);
		
		getSkillTracker().start(Skill.WOODCUTTING);
	}
	
	private enum State {
		CHOP_DOWN,DROP,BANK,WAIT
	}
	
	private State getState() {
		GameObject tree = getGameObjects().closest(gui.getTreeType());
		if (getInventory().isFull())
		{
			int random = Calculations.random(1, 100);
			if(gui.getMethod() == "Bank")
			{
				if(random < 5)
				{
					if(areasTrees.get(gui.getLocation()).contains(getLocalPlayer()))
					{
						return State.CHOP_DOWN;
					}
					else
					{
						return State.BANK;
					}
				}
				else
				{
					return State.BANK;
				}
			}
			else
			{
				if(random < 5)
				{
					return State.CHOP_DOWN;
				}
				else
				{
					return State.DROP;	
				}
			}
		}
		if (tree != null && !getLocalPlayer().isAnimating())
		{
			return State.CHOP_DOWN;
		}
		return State.WAIT;
	}

	public void onExit() {

	}

	@Override
	public int onLoop() {
		if(startScript)
		{
			switch (getState()) 
			{
				case CHOP_DOWN:
					chopdownTree(gui.getTreeType(), areasTrees.get(gui.getLocation()));
					break;
				case DROP:
					drop();					
					break;
				case BANK:
					bank();					
					break;
				case WAIT:
					antiBan(gui.getTreeType(), areasTrees.get(gui.getLocation()));
					sleep(300);
					break;
			}
		}
		return 600;
	}
	
	private final Color color3 = new Color(255, 255, 255);
	private final Font font1 = new Font("Arial", Font.BOLD, 13);
	private final Font font2 = new Font("Arial", Font.BOLD, 0);
	private final Font font3 = new Font("Arial", 0, 13);
	private Timer t = new Timer();
	
	@Override
	public void onPaint(Graphics g1)
	{
		switch(gui.getTreeType())
		{
			case "Tree":
				logsxp = 25;
				break;
			case "Oak":
				logsxp = 37.5;
				break;
			case "Willow":
				logsxp = 67.5;
				break;
			case "Yew":
				logsxp = 175;
				break;
		}
		
		logsgained = (int) Math.floor(getSkillTracker().getGainedExperience(Skill.WOODCUTTING) / logsxp); 
		if (t == null) 
		{
			t = new Timer();
		}
		Graphics2D g = (Graphics2D) g1;
		g.drawImage(img, 10, 210, null);
		g.setFont(font1);
		g.setColor(color3);
		g.setFont(font2);
		g.setFont(font3);
		g.drawString(Timer.formatTime(t.elapsed()), 100, 375);
		g.drawString(getSkills().getRealLevel(Skill.WOODCUTTING)+ "(+"+ getSkillTracker().getGainedLevels(Skill.WOODCUTTING)+ ") XP: " + getSkillTracker().getGainedExperience(Skill.WOODCUTTING)+ "("+ getSkills().getExperienceToLevel(Skill.WOODCUTTING) + ")", 100, 432);
		g.drawString(logsgained + " ["+ t.getHourlyRate(logsgained) + "]", 335, 375);
		g.drawString("" + logprice * logsgained, 335, 432);
	}
	
	public void TileNode(int xplayer, int yplayer, int zplayer, int xlocation, int ylocation, int zlocation) {
        int x;
		int y;
		
		if(xplayer > xlocation)
        {
        	x = -20;
        }
		else 
		{
			x = 20;
		}
		
		if(yplayer > ylocation)
		{
			y = -20;
		}
		else
		{
			y = 20;
		}
		
		if(getWalking().walk(new Tile(xplayer + x, yplayer + y, zplayer)))
		{
			sleep(4000);
		}
    }

	public void setStartScript(boolean startScript)
	{
		this.startScript = startScript;	
		
		switch(gui.getTreeType())
		{
			case "Tree":
				logid = 1511;
				break;
			case "Oak":
				logid = 1521;
				break;
			case "Willow":
				logid = 1519;
				break;
			case "Yew":
				logid = 1516;
				break;
		}
		
		GEPrice exchangeApi = new GEPrice();
        GELookupResult lookupResult = exchangeApi.lookup(logid);
        if(lookupResult != null)
        {
            //System.out.println("Item: " + lookupResult.name + " Price: " + lookupResult.price);
            logprice = lookupResult.price;
        }
	}
	
	private void chopdownTree(String treeSort, Area treeArea)
	{
		if(treeSort.equals("Willow") && treeArea == draynorTreeArea)
		{
			treeArea = draynorWillowArea;
		}
		else if(treeSort.equals("Willow") && treeArea == lumbridgeTreeArea)
		{
			treeArea = lumbridgeWillowArea;
		}
		
		GameObject tree = getGameObjects().closest(gameObject -> gameObject != null && gameObject.getName().equals(treeSort));
		if (tree != null) {
			if(treeArea.contains(getLocalPlayer()))
			{
				sleep(Calculations.random(1000, 2000));
				if(!getLocalPlayer().isAnimating())
				{
					if(tree.interact("Chop down"))
					{
						int countlog = getInventory().count("Logs");
						sleepUntil(() -> getInventory().count("Logs") > countlog || !getLocalPlayer().isAnimating(), 12000);
					}
				}
			}
			else 
			{
				if(getWalking().walk(treeArea.getCenter()))
				{
					sleep(Calculations.random(2000, 3000));
				}
				else
				{
					Tile playertile = getLocalPlayer().getTile();
					Tile treetile = treeArea.getCenter();
					TileNode(playertile.getX(), playertile.getY(), playertile.getZ(), treetile.getX(), treetile.getY(), treetile.getZ());
				}
			}
		}
	}
	
	private void drop()
	{
		if(getInventory().isFull())
		{
			getInventory().dropAll(item -> item != null && item.getName().contains("logs"));
		}
	}
	
	private void bank()
	{
		if(areasBanks.get(gui.getLocation()).contains(getLocalPlayer()))
		{
			NPC banker = getNpcs().closest(npc -> npc != null && npc.hasAction("Bank"));
			if(banker.interact("Bank"))
			{
				if(sleepUntil(() -> getBank().isOpen(), 9000))
				{
					sleep(Calculations.random(1000, 3000));
					if(getBank().depositAllExcept(item -> item != null && item.getName().contains("axe")))
					{
						if(sleepUntil(() -> !getInventory().isFull(), 8000))
						{
							sleep(Calculations.random(1000, 3000));
							if(getBank().close())
							{
								sleepUntil(() -> !getBank().isOpen(), 8000);
							}
						}
					}
				}
			}
		}
		else
		{
			if(getWalking().walk(areasBanks.get(gui.getLocation()).getRandomTile()))
			{
				sleep(Calculations.random(2000, 4000));
			}
			else 
			{
				Tile playertile = getLocalPlayer().getTile();
				Tile banktile = areasBanks.get(gui.getLocation()).getRandomTile();
				TileNode(playertile.getX(), playertile.getY(), playertile.getZ(), banktile.getX(), banktile.getY(), banktile.getZ());
			}
		}
	}
	
	private void antiBan(String treeSort, Area treeArea) { 
		
		if(treeSort.equals("Willow") && treeArea == draynorTreeArea)
		{
			treeArea = draynorWillowArea;
		}
		else if(treeSort.equals("Willow") && treeArea == lumbridgeTreeArea)
		{
			treeArea = lumbridgeWillowArea;
		}
		
		int random = Calculations.random(1, 250);

		if (random == 1) 
		{
			if (!getTabs().isOpen(Tab.STATS)) 
			{
				getTabs().open(Tab.STATS);
				getSkills().hoverSkill(Skill.WOODCUTTING);
				sleep(Calculations.random(1000, 2000));
				getTabs().open(Tab.INVENTORY);
			}
		} 
		else if (random <= 10) 
		{
			if (!getTabs().isOpen(Tab.INVENTORY)) 
			{
				getTabs().open(Tab.INVENTORY);
			}
		} 
		else if (random <= 20) 
		{
			getCamera().rotateToTile(treeArea.getRandomTile());
		} 
		else if (random <= 30) 
		{
			getCamera().rotateToEntity(getLocalPlayer());
		} 
		else if (random <= 108) 
		{
			if (getMouse().isMouseInScreen()) 
			{
				if (getMouse().moveMouseOutsideScreen()) 
				{
					sleep(Calculations.random(1500, 3000));
				}
			}
		} 
		else 
		{
			//
		}
	}
}
