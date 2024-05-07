package com.mefigenia.Pomodoro.App;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
public class PomodoroWatch implements ActionListener  {

    JFrame frame = new JFrame();
    JButton startButton = new JButton("START");
    JButton resetButton = new JButton("RESET");
    JLabel timeLabel = new JLabel();
    JLabel warningLabel = new JLabel("Please Introduce the task");
    final String warning = "Please Introduce the task";
    final String pomodoro = "Time to Study";
    final String resting = "Time to rest";
    JTextField task = new JTextField();
    JLabel textLabel = new JLabel();
    JLabel topicLabel = new JLabel();
    int elapsedTime = 0;
    int seconds =0;
    int minutes =0;
    int minutesVariable = 2;
    int secondsVariable = 60;
    boolean isResting = false;

    boolean isFirstResting = true;
    int hours =0;
    boolean started = false;
    String seconds_string = String.format("%02d", seconds);
    String minutes_string = String.format("%02d", minutes);
    String hours_string = String.format("%02d", hours);
    final int restTime = 5;
    final int restFinalTime =15;
    int numRest = 1;

    @Autowired
    PomodoroDao pomodoroDao;


    Timer timer = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            elapsedTime=elapsedTime+1000;
            hours = (elapsedTime/3600000);
            minutes = minutesVariable - ((elapsedTime/60000) % 60);
            seconds = secondsVariable - ((elapsedTime/1000) % 60);
            seconds_string = String.format("%02d", seconds);
            minutes_string = String.format("%02d", minutes);
            hours_string = String.format("%02d", hours);
            timeLabel.setText(hours_string+":"+minutes_string+":"+seconds_string);

            if (numRest ==5) {
                reset(); }

            if(minutes == 0 && seconds ==1 && !isResting) {
                restingValues();
                ++numRest;
            }
            else if(minutes == 0 && seconds ==1 && isResting) {
                pomodoroValues();

            }

            if(isFirstResting) {
                Pomodoro pomodoro = new Pomodoro();

                pomodoro.setTaskName(task.getText());

                LocalDateTime date = LocalDateTime.now();
                DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String formattedDate = date.format(formatDate);
                pomodoro.setDate(formattedDate);

                if(numRest == 4)
                    pomodoro.setTimeRest(restFinalTime);
                else
                    pomodoro.setTimeRest(restTime);

                //set the number of pomodoro
                pomodoro.setNumPom(numRest);

                //save the pomodoro in the database
                pomodoroDao.save(pomodoro);

                task.setEditable(false);

                isFirstResting = false;
            }

    }

    });

    private void restingValues() {
        secondsVariable = 60;
        elapsedTime = 0;
        isResting = true;
        warningLabel.setText(resting);
        if(numRest == 4)
            minutesVariable = 0; //restFinalTime;
        else
            minutesVariable = 1; //restTime;
    }

    private void pomodoroValues() {

        if(numRest==5)
            timer.restart();
        else {
            minutesVariable = 2;
            secondsVariable = 60;
            elapsedTime = 0;
            isResting = false;
            isFirstResting = true;
            warningLabel.setText(pomodoro);
        }
    }
    PomodoroWatch(){
        textLabel.setText("Pomodoro Watch");
        textLabel.setBounds(0,5,500,50);
        textLabel.setFont(new Font("Verdana",Font.PLAIN,30));
        textLabel.setOpaque(true);
        textLabel.setHorizontalAlignment(JTextField.CENTER);

        topicLabel.setText("Task to study:");
        topicLabel.setBounds(10,50,200,50);
        topicLabel.setFont(new Font("Verdana",Font.PLAIN,25));
        topicLabel.setOpaque(true);
        topicLabel.setHorizontalAlignment(JTextField.LEFT);

        task.setBounds(200,50,300,50);
        task.setFont(new Font("Verdana",Font.PLAIN,25));
        task.setOpaque(true);
        task.setHorizontalAlignment(JTextField.LEFT);

        warningLabel.setBounds(10,250,500,50);
        warningLabel.setFont(new Font("Verdana",Font.PLAIN,20));
        warningLabel.setOpaque(true);
        warningLabel.setHorizontalAlignment(JTextField.LEFT);

        timeLabel.setText(hours_string+":"+minutes_string+":"+seconds_string);
        timeLabel.setBounds(100,100,200,100);
        timeLabel.setFont(new Font("Verdana",Font.PLAIN,35));
        timeLabel.setBorder(BorderFactory.createBevelBorder(1));
        timeLabel.setOpaque(true);
        timeLabel.setHorizontalAlignment(JTextField.CENTER);

        startButton.setBounds(100,200,100,50);
        startButton.setFont(new Font("Ink Free",Font.PLAIN,20));
        startButton.setFocusable(false);
        startButton.addActionListener(this);

        resetButton.setBounds(200,200,100,50);
        resetButton.setFont(new Font("Ink Free",Font.PLAIN,20));
        resetButton.setFocusable(false);
        resetButton.addActionListener(this);

        frame.add(startButton);
        frame.add(resetButton);
        frame.add(timeLabel);
        frame.add(textLabel);
        frame.add(topicLabel);
        frame.add(task);
        frame.add(warningLabel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420,420);
        frame.setLayout(null);
        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {


        if(e.getSource()==startButton) {

            if(numRest ==1 && Objects.equals(task.getText(), "")) {
                warningLabel.setText(warning);
                return;
            }
            else {
                task.setEditable(false);
                warningLabel.setText(pomodoro);
            }

            if(!started) {
                started=true;
                startButton.setText("STOP");

                start();
            }
            else {
                started=false;
                startButton.setText("START");
                stop();
            }


        }
        if(e.getSource()==resetButton) {
            started=false;
            startButton.setText("START");
            reset();
        }

    }

    void start() {
        timer.start();
    }

    void stop() {
        timer.stop();
    }

    void reset() {
        timer.stop();
        elapsedTime=0;
        seconds =0;
        minutes=0;
        hours=0;
        seconds_string = String.format("%02d", seconds);
        minutes_string = String.format("%02d", minutes);
        hours_string = String.format("%02d", hours);
        timeLabel.setText(hours_string+":"+minutes_string+":"+seconds_string);
        task.setEditable(true);
        task.setText("");
        isFirstResting = true;
        numRest = 0;
        warningLabel.setText("");
    }
}
