package tictactoe;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;


class MenuGame extends JMenu {
    MenuGame(String name) {
        super(name);
        setName("Menu" + name);
        setVisible(true);

        JMenuItem[] list = {
                hh = new JMenuItem("Human vs. Human"),
                hr = new JMenuItem("Human vs. Robot"),
                rh = new JMenuItem("Robot vs. Human"),
                rr = new JMenuItem("Robot vs. Robot")
        };
        exit = new JMenuItem("Exit");

        hh.setName("MenuHumanHuman");
        hr.setName("MenuHumanRobot");
        rh.setName("MenuRobotHuman");
        rr.setName("MenuRobotRobot");
        exit.setName("MenuExit");

        for (JMenuItem i : list) {
            i.setVisible(true);
            add(i);
        }

        addSeparator();

        add(exit);
        exit.setVisible(true);
    }

    final JMenuItem hh;
    final JMenuItem hr;
    final JMenuItem rh;
    final JMenuItem rr;
    final JMenuItem exit;
}

class MenuBar extends JMenuBar {
    
    MenuBar() {
        super();
        game = new MenuGame("Game");
        add(game);
        setVisible(true);
    }
    final MenuGame game;
}
