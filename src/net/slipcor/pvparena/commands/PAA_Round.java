package net.slipcor.pvparena.commands;

import java.util.HashSet;

import net.slipcor.pvparena.PVPArena;
import net.slipcor.pvparena.arena.Arena;
import net.slipcor.pvparena.classes.PARound;
import net.slipcor.pvparena.classes.PARoundMap;
import net.slipcor.pvparena.core.Help;
import net.slipcor.pvparena.core.Language;
import net.slipcor.pvparena.core.Help.HELP;
import net.slipcor.pvparena.core.Language.MSG;
import net.slipcor.pvparena.loadables.ArenaGoal;
import net.slipcor.pvparena.core.StringParser;

import org.bukkit.command.CommandSender;

/**
 * <pre>PVP Arena ROUND Command class</pre>
 * 
 * A command to manage arena rounds
 * 
 * @author slipcor
 * 
 * @version v0.10.0
 */

public class PAA_Round extends AbstractArenaCommand {
	
	public PAA_Round() {
		super(new String[] {});
	}

	@Override
	public void commit(final Arena arena, final CommandSender sender, final String[] args) {
		if (!this.hasPerms(sender, arena)) {
			return;
		}

		// /pa [arenaname] round - list rounds
		// /pa [arenaname] round [number] - list round goals
		// /pa [arenaname] round [number] [goal] - toggle round goal
		
		if (!argCountValid(sender, arena, args, new Integer[]{0,1,2})) {
			return;
		}
		
		if (args.length < 1) {
			if (arena.getRoundCount() < 1) {
				arena.msg(sender, Language.parse(MSG.ROUND_DISPLAY, "1", StringParser.joinSet(arena.getGoals(), ", ")));
			} else {
				final PARoundMap roundMap = arena.getRounds();
				for (int i = 0; i < roundMap.getCount(); i ++) {
					arena.msg(sender, Language.parse(MSG.ROUND_DISPLAY, String.valueOf(i+1), StringParser.joinSet(roundMap.getGoals(i),", ")));
				}
			}
			return;
		}
		
		try {
			int round = Integer.parseInt(args[1]);
			final PARoundMap roundMap = arena.getRounds();
			
			if (round >= arena.getRoundCount()) {
				round = arena.getRoundCount();
				
				roundMap.set(round, new PARound(new HashSet<ArenaGoal>()));
			} else if (args.length < 3) {
				arena.msg(sender, Language.parse(MSG.ROUND_DISPLAY, args[1], StringParser.joinSet(roundMap.getGoals(round),", ")));
				return;
			}
			
			ArenaGoal goal = null;
			
			if (args.length > 2) {
				goal = PVPArena.instance.getAgm().getGoalByName(args[2].toLowerCase());
			}
			
			if (goal == null) {
				arena.msg(sender, Language.parse(MSG.ERROR_GOAL_NOTFOUND, args[2], StringParser.joinSet(PVPArena.instance.getAgm().getAllGoalNames(), " ")));
				arena.msg(sender, Language.parse(MSG.GOAL_INSTALLING));
				return;
			}

			final PARound rRound = roundMap.getRound(round);
			
			if (rRound.toggle(arena, goal)) {
				// added
				arena.msg(sender, Language.parse(MSG.ROUND_ADDED, goal.getName()));
			} else {
				// removed
				arena.msg(sender, Language.parse(MSG.ROUND_REMOVED, goal.getName()));
			}
			
			roundMap.set(round, rRound);
			//TODO LATER
			
		} catch (NumberFormatException e) {
			arena.msg(sender, Language.parse(MSG.ERROR_NOT_NUMERIC, args[1]));
			
		} catch (Exception e) {
			e.printStackTrace();
			arena.msg(sender, Language.parse(MSG.ERROR_ERROR, e.getLocalizedMessage()));
			
		}
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

	@Override
	public void displayHelp(final CommandSender sender) {
		Arena.pmsg(sender, Help.parse(HELP.ROUND));
	}
}
