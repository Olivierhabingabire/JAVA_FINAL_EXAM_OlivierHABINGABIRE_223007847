package forms;

import java.awt.*;
import java.util.Objects;

import javax.swing.*;

import admin.MatchesPanel;
import admin.PlayersPanel;
import admin.RefereesPanel;
import admin.SportsPanel;
import admin.TeamsPanel;
import admin.UsersPanel;
import coaches.CoachHomePanel;
import coaches.CoachMatchesPanel;
import coaches.CoachPlayersPanel;
import coaches.CoachTeamPanel;
import fans.FanHomePanel;
import fans.FanMatchesPanel;
import fans.FanPlayersPanel;
import fans.FanSportsPanel;
import fans.FanTeamsPanel;

public class SportPlatformSystem extends JFrame {

    private JTabbedPane pane;
    String role;
    int user_id;
 
    public SportPlatformSystem(String role, int user_id) {
    	this.role = role;
    	this.user_id = user_id; 
   
    
        setTitle("SPORTS PLATFORM SYSTEM");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        createUI();
        add(pane, BorderLayout.CENTER);

        setVisible(true);
    }
    

    private void createUI() {
        pane = new JTabbedPane();
        switch(role) {
        case "admin":
        	
            pane.addTab("Users", new UsersPanel());
            pane.addTab("Teams", new TeamsPanel());
            pane.addTab("Sports", new SportsPanel());
            pane.addTab("Players",new PlayersPanel());
            pane.addTab("Matches", new MatchesPanel());
            pane.addTab("Referees", new RefereesPanel());
            break;
        case "fan":
        	   pane.addTab("Home", new FanHomePanel());
               pane.addTab("Teams", new FanTeamsPanel());
               pane.addTab("Sports", new FanSportsPanel());
               pane.addTab("Players",new FanPlayersPanel());
               pane.addTab("Matches", new FanMatchesPanel());
               break;
        case "coach" :
        	   pane.addTab("Home", new CoachHomePanel(user_id));
               pane.addTab("Team", new CoachTeamPanel(user_id));
               pane.addTab("Players",new CoachPlayersPanel(user_id));
               pane.addTab("Matches", new CoachMatchesPanel(user_id));        
        }
   
    }

 

}
