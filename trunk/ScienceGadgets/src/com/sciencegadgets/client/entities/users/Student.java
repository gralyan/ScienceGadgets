package com.sciencegadgets.client.entities.users;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.sciencegadgets.client.algebra.transformations.Skill;
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
		skills.put(skill, newLevel);
		
		HashSet<Badge> earnedBadges = Badge.getBadgesEarned(skill, newLevel);
		for(Badge earnedBadge : earnedBadges) {
			boolean newBadge = badges.add(earnedBadge);
			if(!newBadge) {
				earnedBadges.remove(earnedBadge);
			}
		}
		
		return earnedBadges;
	}
	
	public boolean hasBadge(Badge badge) {
		return badges.contains(badge);
	}
	
	public boolean hasBadges(HashSet<Badge> badges) {
		return badges.containsAll(badges);
	}

}