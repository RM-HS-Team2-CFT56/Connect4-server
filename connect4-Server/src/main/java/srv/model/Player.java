package srv.model;

import srv.model.Enums.PlayerStatus;

public class Player {

    private long id;
    private String playerName;
    
    //private PlayerStatus playerStatus; 
    
    /*

    public PlayerStatus getPlayerStatus() {
		return playerStatus;
	}

	public void setPlayerStatus(PlayerStatus playerStatus) {
		this.playerStatus = playerStatus;
	} */

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

    public long getId() {
        return id;
    }
    
    public void setId(long id) {
    	this.id = id;
    }
    
    public Player(long id, String content) {
        this.id = id;
        this.playerName = content;
        //this.playerStatus = ;
    }
    
    public Player() {
        this.id = 0;
        this.playerName = "no_name";
    }
    
    public Player(Player another) {
    	
        this.id = another.getId(); 
        this.playerName = another.getPlayerName();
        
      }
    
    
}
