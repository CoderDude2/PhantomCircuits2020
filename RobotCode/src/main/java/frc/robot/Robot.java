/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {

  /* Joysticks */
  Joystick gamepad = new Joystick(0);

  /* Power Distribution Panel */
  PowerDistributionPanel pdp = new PowerDistributionPanel();// add pdp functions

  /* Variables */
  int shooterrightid = 12;
  int shooterleftid = 13;
  double shooterspeed = 0;
  public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput, maxRPM;

  /* Shooter Motors */
  CANSparkMax shooterright = new CANSparkMax(shooterrightid, MotorType.kBrushless);
  CANSparkMax shooterleft = new CANSparkMax(shooterleftid, MotorType.kBrushless);

  /* Shooter Encoders */
  CANEncoder shooterrightencoder = new CANEncoder(shooterright);
  CANEncoder shooterleftencoder = new CANEncoder(shooterleft);

  /* Shooter PID */
  CANPIDController shooterrightPID = new CANPIDController(shooterright);
  CANPIDController shooterleftPID = new CANPIDController(shooterleft);

  @Override
  public void robotInit() {
    /* Set up shooter for 2 motors, one on each side, individually no followers currently */
    shooterright.restoreFactoryDefaults();
    shooterleft.restoreFactoryDefaults();
    shooterright.setInverted(false);
    shooterleft.setInverted(true);

    // PID coefficients
    kP = 6e-5; 
    kI = 0;
    kD = 0; 
    kIz = 0; 
    kFF = 0.000015; 
    kMaxOutput = 1; 
    kMinOutput = 0;
    maxRPM = 5700;

    // set PID coefficients
    shooterrightPID.setP(kP);
    shooterrightPID.setI(kI);
    shooterrightPID.setD(kD);
    shooterrightPID.setIZone(kIz);
    shooterrightPID.setFF(kFF);
    shooterrightPID.setOutputRange(kMinOutput, kMaxOutput);

    shooterleftPID.setP(kP);
    shooterleftPID.setI(kI);
    shooterleftPID.setD(kD);
    shooterleftPID.setIZone(kIz);
    shooterleftPID.setFF(kFF);
    shooterleftPID.setOutputRange(kMinOutput, kMaxOutput);

    // display PID coefficients on SmartDashboard
    SmartDashboard.putNumber("P Gain", kP);
    SmartDashboard.putNumber("I Gain", kI);
    SmartDashboard.putNumber("D Gain", kD);
    SmartDashboard.putNumber("I Zone", kIz);
    SmartDashboard.putNumber("Feed Forward", kFF);
    SmartDashboard.putNumber("Max Output", kMaxOutput);
    SmartDashboard.putNumber("Min Output", kMinOutput);

    SmartDashboard.putNumber("Shooter Speed", 0);

  }
  
  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("Total", pdp.getCurrent(0) + pdp.getCurrent(1) + pdp.getCurrent(2) + pdp.getCurrent(3) + pdp.getCurrent(4) + pdp.getCurrent(5) + pdp.getCurrent(6) + pdp.getCurrent(7) + pdp.getCurrent(8) + pdp.getCurrent(9) + pdp.getCurrent(10) + pdp.getCurrent(11) + pdp.getCurrent(12) + pdp.getCurrent(13) + pdp.getCurrent(14) + pdp.getCurrent(15));
    SmartDashboard.putNumber("Right RPM", shooterrightencoder.getVelocity());
    SmartDashboard.putNumber("Left RPM", shooterrightencoder.getVelocity());
    shooterspeed = SmartDashboard.getNumber("Shooter Speed", 0);

    // read PID coefficients from SmartDashboard
    double p = SmartDashboard.getNumber("P Gain", 0);
    double i = SmartDashboard.getNumber("I Gain", 0);
    double d = SmartDashboard.getNumber("D Gain", 0);
    double iz = SmartDashboard.getNumber("I Zone", 0);
    double ff = SmartDashboard.getNumber("Feed Forward", 0);
    double max = SmartDashboard.getNumber("Max Output", 0);
    double min = SmartDashboard.getNumber("Min Output", 0);

    // if PID coefficients on SmartDashboard have changed, write new values to controller
    if((p != kP)) { shooterrightPID.setP(p); shooterleftPID.setP(p); kP = p; }
    if((i != kI)) { shooterrightPID.setI(i); shooterleftPID.setI(i); kI = i; }
    if((d != kD)) { shooterrightPID.setD(d); shooterleftPID.setD(d); kD = d; }
    if((iz != kIz)) { shooterrightPID.setIZone(iz); shooterleftPID.setIZone(iz); kIz = iz; }
    if((ff != kFF)) { shooterrightPID.setFF(ff); shooterleftPID.setFF(ff); kFF = ff; }
    if((max != kMaxOutput) || (min != kMinOutput)) { 
      shooterrightPID.setOutputRange(min, max); 
      shooterleftPID.setOutputRange(min, max); 
      kMinOutput = min; kMaxOutput = max; 
    }

  }

  @Override
  public void autonomousInit() {
  }
  
  @Override
  public void autonomousPeriodic() {
  }

  @Override    
  public void teleopInit() {
  }

  @Override
  public void teleopPeriodic() {
    if (gamepad.getRawButton(6)) {
      shooterright.set(shooterspeed);
    }
    else if (gamepad.getRawButton(5)) {
      shooterleft.set(shooterspeed);
    }
    else if (gamepad.getRawButton(3)) {
      shooterright.set(shooterspeed);
      shooterleft.set(shooterspeed);
    }
    else if (gamepad.getRawButton(4)) {
      double setPoint = shooterspeed*maxRPM;
      shooterrightPID.setReference(setPoint, ControlType.kVelocity);
      shooterleftPID.setReference(setPoint, ControlType.kVelocity);
      SmartDashboard.putNumber("SetPoint", setPoint);
    }
    else {
      shooterright.set(0);
      shooterleft.set(0);
    }


  }
  
  @Override
  public void disabledInit() {
    
  }
}
