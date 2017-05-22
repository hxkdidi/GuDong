package cn.test.gudong.db.entity;

public class MyGame {
	
	private String username;
	private String title;
	private int game_id;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getGame_id() {
		return game_id;
	}
	public void setGame_id(int game_id) {
		this.game_id = game_id;
	}
	@Override
	public String toString() {
		return "MyGame [username=" + username + ", title=" + title + ", game_id=" + game_id + "]";
	}
	

}
