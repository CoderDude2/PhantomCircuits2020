/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.*;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends TimedRobot {
  private DifferentialDrive myRobot;

  /* Joysticks */
  Joystick gamepad = new Joystick(0);
  AHRS gyro = new AHRS();

  /* Power Distribution Panel */
  PowerDistributionPanel pdp = new PowerDistributionPanel();// add pdp functions

  /* Motor Controller IDs */
  int leftfrontmotorid = 14;
  int leftrearmotorid = 15;
  int rightfrontmotorid = 1;
  int rightrearmotorid = 0;
  int armmotorid = 3;
  boolean wasturning = false;
  boolean wasstraight = false;

  /* Master Motors */
  WPI_VictorSPX leftfrontmotor = new WPI_VictorSPX(leftfrontmotorid);
  WPI_VictorSPX rightfrontmotor = new WPI_VictorSPX(rightfrontmotorid);

  /* Follower Motors */
  WPI_VictorSPX leftrearmotor = new WPI_VictorSPX(leftrearmotorid);
  WPI_VictorSPX rightrearmotor = new WPI_VictorSPX(rightrearmotorid);

  /* Arm Motor */
  /* CANSparkMax armmotor = new CANSparkMax(3, MotorType.kBrushed); */
  CANSparkMax armmotor = new CANSparkMax(3, MotorType.kBrushless);
  Compressor c = new Compressor(0);

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

  c.setClosedLoopControl(true);

  /* Set up Dashboard widgets */
}

@Override
public void robotPeriodic() {
if (gamepad.getRawButton(1)) {
  gyro.reset();
}
  SmartDashboard.putBoolean("Straight",wasstraight);
  SmartDashboard.putBoolean("IMU_Connected",gyro.isConnected());
  SmartDashboard.putBoolean("IMU_IsCalibrating",gyro.isCalibrating());
  SmartDashboard.putNumber("IMU_Yaw",gyro.getYaw());
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
  c.setClosedLoopControl(true);
}

@Override
public void autonomousPeriodic() {
  
}

@Override    
public void teleopInit() {
  c.setClosedLoopControl(true);
}

@Override
public void teleopPeriodic() {
  c.setClosedLoopControl(true);
  
  if (gamepad.getRawButton(5)) {
      /*myRobot.tankDrive(1, 1);*/
      if (!wasstraight){
        wasstraight = true;
        gyro.reset();
      }
      accurateDrive(gyro, 0.6, 0, 2);
  } else if (gamepad.getRawButton(6)) {
      myRobot.tankDrive(-1, -1);
      if (!wasstraight){
        wasstraight = true;
        gyro.reset();
      }
      accurateDrive(gyro, -0.6, 0, 2);
  } else {
      wasstraight = false;
      myRobot.tankDrive(gamepad.getRawAxis(1)*1, gamepad.getRawAxis(5)*1);
  }

}

@Override
public void disabledInit() {
  c.setClosedLoopControl(false);
}

public void accurateDrive(AHRS ahrs, double speed, double targetAngle, int tolerence) {
  if(ahrs.getYaw() < targetAngle - tolerence) {
    System.out.println("Too far left");
    myRobot.tankDrive(speed, speed / 2);
  }
  else if(ahrs.getYaw() > targetAngle + tolerence) {
    System.out.println("Too far right");
    myRobot.tankDrive(speed / 2, speed);
  }
  else {
    System.out.println("Good");
    myRobot.tankDrive(speed, speed);
  }
}
