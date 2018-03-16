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
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;

@ScriptManifest(author = "Zandpaad", name = "Combat", version = 1.0, description = "Combat Script", category = Category.COMBAT)

public class Main extends AbstractScript{
	
	private Area cowArea = new Area(3157,3344, 3193, 3309, 0);
	private Area lumbridgeGroundFloorBankArea = new Area(3206, 3209, 3207, 3210, 0);
	private Area lumbridgeFirstFloorBankArea = new Area(3206, 3209, 3207, 3210, 1);
	private Area lumbridgeSecondFloorBankArea = new Area(3206, 3209, 3207, 3210, 2);
	
	ArrayList<Area> lumbridgeBankArea = new ArrayList<Area>();

	private NPC currentNpc;
	public String npcName = "Cow";
	private String food = "Shrimps";
	private int floorLevel = 0;
	private int npcKilled = 0;
	private int cowhidePrice = 68;
	
	private BufferedImage img;
	
	public void onStart() {
		lumbridgeBankArea.add(lumbridgeGroundFloorBankArea);
		lumbridgeBankArea.add(lumbridgeFirstFloorBankArea);
		lumbridgeBankArea.add(lumbridgeSecondFloorBankArea);
		
		try {
			img = ImageIO.read(getClass().getResourceAsStream("/cmb.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onExit() {

	}

	@Override
	public int onLoop() {
		if(getCombat().getHealthPercent() > 31)
		{
			if(cowArea.contains(getLocalPlayer()))
			{
				currentNpc = getNpcs().closest(npc -> npc != null && npc.getName() != null && npc.getName().contains("Cow") && !npc.isInCombat() && npc.getInteractingCharacter() == null);
				attack(currentNpc);
			}
			else 
			{
				getWalking().walk(cowArea.getRandomTile());
			}
		}
		else 
		{
			if(getInventory().contains(food))
			{
				eat();
			}
			else
			{
				if(lumbridgeBankArea.get(floorLevel).contains(getLocalPlayer()))
				{
					bankLumbridge();
				}
				else 
				{
					getWalking().walk(lumbridgeBankArea.get(0).getRandomTile());
				}
			}
		}
		return Calculations.random(600, 3000);
	}
	
	private void eat()
	{
		if(food != null)
		{
			getInventory().interact(food, "eat");
		}
		sleep(2000);
	}
	
	private void attack(NPC currentNpc)
	{
		if(currentNpc != null && !getLocalPlayer().isInCombat()) 
        {
            currentNpc.interact("Attack");
            npcKilled++;
        }
	}
	
	private void bankLumbridge()
	{
		GameObject staircase = getGameObjects().closest("Staircase");
		if(staircase != null)
		{
			if(!getInventory().contains(food) && floorLevel < 2)
			{
				staircase.interact("Climb-up");
				floorLevel++;
			}
			else if(getInventory().contains(food) && floorLevel > 0)
			{
				staircase.interact("Climb-down");
				floorLevel--;
			}
			
			if(floorLevel == 2)
			{
				GameObject banker = getGameObjects().closest(f -> f != null && f.getName().equalsIgnoreCase("Bank booth") && f.hasAction("Bank"));
				if(banker.interact("Bank"))
				{
					if(sleepUntil(() -> getBank().isOpen(), 9000))
					{
						if(getBank().withdraw(food, 10))
						{
							sleep(Calculations.random(2000, 3000));										
							if(getBank().close())
							{
								sleepUntil(() -> !getBank().isOpen(), 8000);
							}
						}
					}
				}
			}
		}
	}
	
	private final Color color3 = new Color(255, 255, 255);
	private final Font font1 = new Font("Arial", Font.BOLD, 13);
	private final Font font2 = new Font("Arial", Font.BOLD, 0);
	private final Font font3 = new Font("Arial", 0, 13);
	private Timer t = new Timer();
	
	@Override
	public void onPaint(Graphics g1)
	{
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
		g.drawString(getSkills().getRealLevel(Skill.STRENGTH)+ "(+"+ getSkillTracker().getGainedLevels(Skill.STRENGTH)+ ") XP: " + getSkillTracker().getGainedExperience(Skill.STRENGTH)+ "("+ getSkills().getExperienceToLevel(Skill.STRENGTH) + ")", 100, 432);
		g.drawString(npcKilled + " ["+ t.getHourlyRate(npcKilled) + "]", 335, 375);
		g.drawString("" + cowhidePrice * npcKilled, 335, 432);
	}
}
