package fishing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.utilities.Timer;

@ScriptManifest(author = "Zandpaad", name = "Fishing", version = 1.0, description = "Fishing Script", category = Category.FISHING)

public class MainClass extends AbstractScript{
	
	private Area lumbridgeBankArea = new Area(3200, 3198, 3246, 3238, 0);
	private Area draynorBankArea = new Area(3093, 3241, 3096, 3245, 0);
	
	private Area lumbridgeFishArea = new Area(3200, 3198, 3246, 3238, 0);
	private Area draynorFishArea = new Area(3084, 3234, 3088, 3222, 0);
	
	ArrayList<Area> areasFishingSpots = new ArrayList<Area>();
	ArrayList<Area> areasBanks = new ArrayList<Area>();
	
	private boolean startScript;
	private GUI gui;
	
	private int fishgained;
	private double fishxp;
	
	public void onStart() {
		gui = new GUI(this);
		gui.setVisible(true);
		
		areasBanks.add(lumbridgeBankArea);
		areasBanks.add(draynorBankArea);
		
		areasFishingSpots.add(lumbridgeFishArea);
		areasFishingSpots.add(draynorFishArea);
		
		getSkillTracker().start(Skill.FISHING);
		fishxp = 25;
	}

	public void onExit() {

	}
	
	private enum State
	{
		FISH,DROP,BANK,WAIT
	}
	
	private State getState()
	{
		NPC spot = getNpcs().closest(gameObject -> gameObject != null && gameObject.getName().equals("Fishing spot"));
		if(getInventory().isFull())
		{
			int random = Calculations.random(1, 100);
			if(gui.getMethod() == "Bank")
			{
				if(random < 5)
				{
					if(areasFishingSpots.get(gui.getLocation()).contains(getLocalPlayer()))
					{
						return State.FISH;
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
					return State.FISH;
				}
				else
				{
					return State.DROP;	
				}
			}
		}

		if (spot != null && !getLocalPlayer().isAnimating())
		{
			return State.FISH;
		}
		return State.WAIT;
	}

	@Override
	public int onLoop() {
		if(startScript)
		{
			switch(getState())
			{
			case FISH:
				fishSpot(areasFishingSpots.get(gui.getLocation()));
				break;
			case DROP:
				drop();				
				break;
			case BANK:
				bank();				
				break;
			case WAIT:
				if (getDialogues().canContinue()){
					sleep(Calculations.random(1000, 2000));
		            if(getDialogues().continueDialogue()){
		                sleepUntil(() -> !getDialogues().canContinue(), 3000);
		            }
		        }
				antiBan(areasFishingSpots.get(gui.getLocation()));
				sleep(300);
				break;
			}
		}
		return Calculations.random(600, 3000);
	}
	
	private final Color color1 = new Color(100, 100, 51, 147);
	private final Color color2 = new Color(30, 70, 19);
	private final Color color3 = new Color(255, 255, 255);
	private final BasicStroke stroke1 = new BasicStroke(5);
	private final Font font1 = new Font("Arial", Font.BOLD, 13);
	private final Font font2 = new Font("Arial", Font.BOLD, 0);
	private final Font font3 = new Font("Arial", 0, 13);
	private Timer t = new Timer();
	
	@Override
	public void onPaint(Graphics g1)
	{
		fishgained = (int) Math.floor(getSkillTracker().getGainedExperience(Skill.FISHING) / fishxp); 
		if (t == null) 
		{
			t = new Timer();
		}
		Graphics2D g = (Graphics2D) g1;
		Stroke stroke = g.getStroke();
		g.setColor(color1);
		g.fillRect(3, 4, 205, 185);
		g.setColor(color2);
		g.setStroke(stroke1);
		g.drawRect(3, 4, 205, 185);
		g.setFont(font1);
		g.setColor(color3);
		g.drawString(getManifest().name() + "         " + "v"				+ getManifest().version(), 12, 29);
		g.setFont(font2);
		g.setFont(font3);
		g.drawString("Time running: " + Timer.formatTime(t.elapsed()), 12, 59);
		g.drawString("Levels gained: " + getSkills().getRealLevel(Skill.FISHING)+ "(+"+ getSkillTracker().getGainedLevels(Skill.FISHING)+ ")", 12, 79);
		g.drawString("XP gained: " + getSkillTracker().getGainedExperience(Skill.FISHING)+ "("+ getSkillTracker().getGainedExperiencePerHour(Skill.FISHING) + ")", 12, 99);
		g.drawString("XP to level: "+ getSkills().getExperienceToLevel(Skill.FISHING),12, 121);
		g.drawString("Fished [P/H]: " + fishgained + " ["+ t.getHourlyRate(fishgained) + "]", 12, 141);
		g.setStroke(stroke);
	}
	
	public void setStartScript(boolean startScript)
	{
		this.startScript = startScript;	
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
	
	private void drop()
	{
		if(getInventory().isFull())
		{
			getInventory().dropAll(item -> item != null && item.getName().contains("Raw "));
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
					if(getBank().depositAllExcept(item -> item != null && item.getName().contains("net")))
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
	
	private void fishSpot(Area fishArea)
	{		
		NPC spot = getNpcs().closest(gameObject -> gameObject != null && gameObject.getName().equals("Fishing spot"));
		if (spot != null) {
			if(fishArea.contains(getLocalPlayer()))
			{
				sleep(Calculations.random(500, 2000));
				if(spot.interact("Net") || spot.interact("Small Net"))
				{
					int countshrimps = getInventory().count("Raw shrimps");
					int countanchovies = getInventory().count("Raw anchovies");
					sleepUntil(() -> (getInventory().count("Raw shrimps") + getInventory().count("Raw anchovies")) > (countshrimps + countanchovies), 12000);
				}
				
				if (getDialogues().canContinue()){
		            if(getDialogues().continueDialogue()){
		                sleepUntil(() -> !getDialogues().canContinue(), 3000);
		            }
		        }
			}
			else 
			{
				if(getWalking().walk(fishArea.getCenter()))
				{
					sleep(Calculations.random(2000, 3000));
				}
				else
				{
					Tile playertile = getLocalPlayer().getTile();
					Tile fishtile = fishArea.getCenter();
					TileNode(playertile.getX(), playertile.getY(), playertile.getZ(), fishtile.getX(), fishtile.getY(), fishtile.getZ());
				}
			}
		}
	}
	
	private void antiBan(Area fishingSpotArea) 
	{ 		
		int random = Calculations.random(1, 250);

		if (random == 1) 
		{
			if (!getTabs().isOpen(Tab.STATS)) 
			{
				getTabs().open(Tab.STATS);
				getSkills().hoverSkill(Skill.FISHING);
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
			getCamera().rotateToTile(fishingSpotArea.getRandomTile());
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
