package display;

import chatting.ListeningThread;
import chatting.chatting_client;
import function.ImgSetSize;
import function.get_data;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class invite extends JFrame{
    private static ArrayList<String> List= new ArrayList<String>();
    private static ArrayList<String> friend_list = new ArrayList<String>();
    //
    private JPanel main;
    private JButton create;
    private JScrollPane invite_scroll;
    private JPanel scoll;
    private JTextField search_friend;
    private JButton searchButton;
    private JLabel invite_list;
    private JButton exitButton;
    private JButton add_friend;

    private String user_id;
    private chatting_client client;
    public invite(int session, chatting_client client, String user_id, ListeningThread t1){
        this.client = client;
        this.user_id = user_id;
        setContentPane(main);

        setSize(850, 1000);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // client에서 list 받아오기 get_friend_list();

        get_data getData = new get_data();
        getData.setType54(user_id);
        getData.start();
        friend_list = getData.getList();

        invite_scroll.getVerticalScrollBar().setUnitIncrement(15);

        GridBagLayout Gbag = new GridBagLayout();
        scoll.setLayout(Gbag);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        for(int i = 0;i<friend_list.size();i++){
            friend pane = new friend(friend_list.get(i));
            gbc.fill = GridBagConstraints.BOTH;
            gbc.ipadx = 850;
            gbc.ipady = 100;
            gbc.gridx = 0;
            gbc.gridy = i;
            Gbag.setConstraints(pane,gbc);
            scoll.add(pane);
            scoll.updateUI();
        }
        invite_scroll.setViewportView(scoll);
        invite_scroll.setVisible(true);
        scoll.setVisible(true);

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dm a = new dm(session,client,user_id,t1);
                a.setVisible(true);
                dispose();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = search_friend.getText();
                scoll.removeAll();
                for(int i = 0;i< friend_list.size();i++){
                    if(friend_list.get(i).contains(email)){
                        friend pane = new friend(friend_list.get(i));
                        gbc.fill = GridBagConstraints.BOTH;
                        gbc.ipadx = 850;
                        gbc.ipady = 100;
                        gbc.gridx = 0;
                        gbc.gridy = i;
                        Gbag.setConstraints(pane,gbc);
                        scoll.add(pane);
                        scoll.updateUI();
                    }
                }
                invite_scroll.setViewportView(scoll);
                invite_scroll.setVisible(true);
                scoll.setVisible(true);
            }
        });
        create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int i = 0;i< List.size();i++){
                    System.out.println(List.get(i));
                }
                //chatting_client에 List 전달
                client.make_room(1,user_id,List);
                get_data getData = new get_data();
                getData.setType11(11, user_id);
                getData.start();
                ArrayList<String> b = getData.getMy_room_list();
                System.out.println("chatting_data/" + b.get(b.size()-1) + ".txt");
                File file =new File("chatting_data/" + b.get(b.size()-1) + ".txt");
                try{
                    FileWriter fw =new FileWriter(file,true);
                    BufferedWriter bw= new BufferedWriter(fw);
                    bw.close();
                }
                catch(IOException e2){
                    e2.printStackTrace();
                }

                List.clear();

                dm a = new dm(session,client,user_id,t1);
                a.setVisible(true);
                dispose();
            }
        });
        add_friend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                add_friend a = new add_friend(session,client,user_id,t1);
                a.setVisible(true);
                dispose();
            }
        });
    }

    public class friend extends JPanel{

        private JButton invite_friend;
        private JButton remove_friend;
        private JLabel friend_name;
        private String friend_id;

        public friend(String friend_id){
            this.friend_id = friend_id;

            setLayout(new FlowLayout(FlowLayout.LEFT));

            setSize(850,100);
            invite_friend = new JButton(friend_id+" 초대");
            ImgSetSize invite = new ImgSetSize("src/IMG/invite_invite.png", 50, 50);
            invite_friend.setIcon(invite.getImg());
            invite_friend.setBackground(new Color(255,255,255));

            remove_friend = new JButton("remove");
            ImgSetSize remove = new ImgSetSize("src/IMG/invite_exit.png", 50, 50);
            remove_friend.setIcon(remove.getImg());
            remove_friend.setBackground(new Color(255,255,255));

            friend_name = new JLabel();
            friend_name.setText("");
            //friend_name.setSize(100,50);

            add(invite_friend);
            add(remove_friend);
            add(friend_name);
            setVisible(true);
            invite_friend.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int same = 0;
                    for(int i = 0;i< List.size();i++){
                        if(List.get(i) == friend_id){
                            same = 1;
                        }
                    }
                    if(same == 0){
                        List.add(friend_id);
                        invite_list.setText(invite_list.getText() + friend_id + " , ");
                    }
                }
            });

            remove_friend.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for(int i =0;i<List.size();i++){
                        if(List.get(i) == friend_id){
                            List.remove(i);
                            invite_list.setText(invite_list.getText().replace(friend_id + " , ",""));
                        }
                    }
                }
            });
        }
    }
}
