/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.ctre.phoenix.motorcontrol.InvertType; 
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
/*
import com.kauailabs.navx.frc.*;
import com.kauailabs.navx.*;*/

public class Robot extends TimedRobot {
  private DifferentialDrive myRobot;

  /* Joysticks */
  Joystick gamepad = new Joystick(0);

  /* Power Distribution Panel */
  PowerDistributionPanel pdp = new PowerDistributionPanel();// add pdp functions

  /* Motor Controller IDs */
  int leftfrontmotorid = 14;
  int leftrearmotorid = 15;
  int rightfrontmotorid = 1;
  int rightrearmotorid = 0;
  int armmotorid = 3;

  //This has not been set yet I am assuming. How would I set up CAN ids?
  int compressorID = 0;
 
  private double startTime;

  /* Master Motors */
	WPI_VictorSPX leftfrontmotor = new WPI_VictorSPX(leftfrontmotorid);
	WPI_VictorSPX rightfrontmotor = new WPI_VictorSPX(rightfrontmotorid);
  Compressor comp = new Compressor(compressorID);
	/* Follower Motors */
	WPI_VictorSPX leftrearmotor = new WPI_VictorSPX(leftrearmotorid);
	WPI_VictorSPX rightrearmotor = new WPI_VictorSPX(rightrearmotorid);
  /* Arm Motor */
  CANSparkMax armmotor = new CANSparkMax(armmotorid, MotorType.kBrushed);
  
  @Override
  public void robotInit() {
    /* Set up drive system for 4 motors, one master on each side, each with a follower */
    myRobot = new DifferentialDrive(leftfrontmotor, rightfrontmotor);
    leftrearmotor.follow(leftfrontmotor);
    rightrearmotor.follow(rightfrontmotor);
    leftfrontmotor.setInverted(true); // <<<<<< Adjust this until robot drives forward when stick is forward
		rightfrontmotor.setInverted(true); // <<<<<< Adjust this until robot drives forward when stick is forward
		leftrearmotor.setInverted(InvertType.FollowMaster);
    rightrearmotor.setInverted(InvertType.FollowMaster);
    
    CameraServer.getInstance().startAutomaticCapture();

    

    comp.setClosedLoopControl(true);
   

    /* Set up Dashboard widgets */
    SmartDashboard.putNumber("L Stick", gamepad.getRawAxis(1));
		SmartDashboard.putNumber("R Stick", gamepad.getRawAxis(5));
		SmartDashboard.putBoolean("Back Button", gamepad.getRawButton(5));
		SmartDashboard.putBoolean("Fwd Button", gamepad.getRawButton(6));
		
    SmartDashboard.putNumber("LF", pdp.getCurrent(leftfrontmotorid));
		SmartDashboard.putNumber("LR", pdp.getCurrent(leftrearmotorid));
		SmartDashboard.putNumber("RF", pdp.getCurrent(rightfrontmotorid));
		SmartDashboard.putNumber("RR", pdp.getCurrent(rightrearmotorid));
    SmartDashboard.putNumber("Total", pdp.getCurrent(0) + pdp.getCurrent(1) + pdp.getCurrent(2) + pdp.getCurrent(3) + pdp.getCurrent(4) + pdp.getCurrent(5) + pdp.getCurrent(6) + pdp.getCurrent(7) + pdp.getCurrent(8) + pdp.getCurrent(9) + pdp.getCurrent(10) + pdp.getCurrent(11) + pdp.getCurrent(12) + pdp.getCurrent(13) + pdp.getCurrent(14) + pdp.getCurrent(15));
  }
  
  @Override
  public void autonomousInit() {
    startTime = Timer.getFPGATimestamp();
  }

  public void driveSetter(double leftspeed, double rightspeed){
    leftfrontmotor.set(leftspeed);
    rightfrontmotor.set(-rightspeed);
  }
  
  @Override
  public void autonomousPeriodic() {
    double time = Timer.getFPGATimestamp();

    if(time - startTime < 3){
      driveSetter(0.3,0.3);
    } else {
      driveSetter(0,0);
    }
    
  }

  @Override    
  public void teleopInit() {

  }

  @Override
  public void teleopPeriodic() {
    if (gamepad.getRawButton(5)) {
        myRobot.tankDrive(1, 1);
    } else if (gamepad.getRawButton(6)) {
        myRobot.tankDrive(-1, -1);
    } else {
        myRobot.arcadeDrive(gamepad.getRawAxis(1)*0.75, gamepad.getRawAxis(5)*0.75);
    }

    SmartDashboard.putNumber("L Stick", gamepad.getRawAxis(1));
    SmartDashboard.putNumber("R Stick", gamepad.getRawAxis(5));
    SmartDashboard.putBoolean("Back Button", gamepad.getRawButton(5));
    SmartDashboard.putBoolean("Fwd Button", gamepad.getRawButton(6));
  
    SmartDashboard.putNumber("LF", pdp.getCurrent(leftfrontmotorid));
    SmartDashboard.putNumber("LR", pdp.getCurrent(leftrearmotorid));
    SmartDashboard.putNumber("RF", pdp.getCurrent(rightfrontmotorid));
    SmartDashboard.putNumber("RR", pdp.getCurrent(rightrearmotorid));
    SmartDashboard.putNumber("Total", pdp.getCurrent(0) + pdp.getCurrent(1) + pdp.getCurrent(2) + pdp.getCurrent(3) + pdp.getCurrent(4) + pdp.getCurrent(5) + pdp.getCurrent(6) + pdp.getCurrent(7) + pdp.getCurrent(8) + pdp.getCurrent(9) + pdp.getCurrent(10) + pdp.getCurrent(11) + pdp.getCurrent(12) + pdp.getCurrent(13) + pdp.getCurrent(14) + pdp.getCurrent(15));
  }
  

  @Override
  public void disabledInit() {
    comp.setClosedLoopControl(false); }
  
  }