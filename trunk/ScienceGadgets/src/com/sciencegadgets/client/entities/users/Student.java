package com.sciencegadgets.client.entities.users;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.transformations.Skill;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.shared.dimensions.UnitName;

@Entity
public class Student implements Serializable{
	private static final long serialVersionUID = 5873355830196332379L;

	@Id
	String name;
	
	HashMap<Skill, Integer> skills;
	HashSet<Badge> badges;

	public Student(String name) {
		this.name=name;
		skills = new HashMap<Skill, Integer>();
		badges = new HashSet<Badge>();
		badges.add(Badge.ADD);
	}

	public UnitName getName() {
		return new UnitName(name);
	}
	
	public HashMap<Skill, Integer> getSkills() {
		return skills;
	}
	
	public HashSet<Badge> getBadges() {
		return badges;
	}
	
	public Integer getSkillLevel(Skill skill) {
		return skills.get(skill);
	}
	
	/**
	 * Increases the student's skill level, and adds all the badges they've earned.
	 * @param skill
	 * @param increase
	 * @return - The new badges this increase earned
	 */
	public HashSet<Badge> increaseSkill(Skill skill, int increase) {
		
		Integer prevLevel = skills.get(skill);
		if(prevLevel == null) {
			prevLevel = 0;
		}
		
		int newLevel = prevLevel + increase;
		//Some incorrect answers decrease skill level, don't go below zero
		newLevel = newLevel < 0 ? 0 : newLevel;
		skills.put(skill, newLevel);
		
		HashSet<Badge> earnedBadges = Badge.getBadgesEarned(skill, newLevel);
		HashSet<Badge> newBadges = new HashSet<Badge>();
		
		for(Badge earnedBadge : earnedBadges) {
			boolean isNewBadge = badges.add(earnedBadge);
			if(isNewBadge) {
				newBadges.add(earnedBadge);
				Moderator.equationBrowser.algebraBrowser.addProblemsForNewBadge(earnedBadge);
			}
		}
		
		return newBadges;
	}
	
	public boolean hasBadge(Badge b) {
		return badges.contains(b);
	}
	
	public boolean hasBadges(HashSet<Badge> b) {
		return badges.containsAll(b);
	}

}
