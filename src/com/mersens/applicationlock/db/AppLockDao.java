package com.mersens.applicationlock.db;

import java.util.List;

public interface AppLockDao {
	public boolean find(String name);
	public List<String> findAll();
	public void add(String name);
	public void delete(String name);
}
