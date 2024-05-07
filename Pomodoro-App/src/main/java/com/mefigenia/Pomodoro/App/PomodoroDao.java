package com.mefigenia.Pomodoro.App;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PomodoroDao extends JpaRepository<Pomodoro, Integer> {

}
