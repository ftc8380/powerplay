package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


//also we'll change the values accordingly, these values are placeholders

public class Sliders {
    Servo servoLeftV4b;
    Servo servoRightV4b;
    DcMotor motorLiftLeft;
    DcMotor motorLiftRight;

    public Sliders(DcMotor lm, DcMotor rm, Servo ls, Servo rs) {
        motorLiftLeft = lm;
        motorLiftRight = rm;
        servoLeftV4b = ls;
        servoRightV4b = rs;
        motorLiftRight.setDirection(DcMotorSimple.Direction.REVERSE);
        motorLiftLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLiftRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLiftLeft.setTargetPosition(0);
        motorLiftRight.setTargetPosition(0);
        motorLiftLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorLiftRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        ls.setDirection(Servo.Direction.REVERSE);
    }

    private int rotationsToTicks(double rotations) {
        double ticksPerRotation = 384.5;
        return (int) (rotations * ticksPerRotation);
    }

    public void servoGrouping(double position) {
        servoLeftV4b.setPosition(position);
        servoRightV4b.setPosition(position);
    }

    public void motorGrouping(double rotations){
        motorLiftLeft.setPower(0.5);
        motorLiftRight.setPower(0.5);
        motorLiftLeft.setTargetPosition(rotationsToTicks(rotations));
        motorLiftRight.setTargetPosition(rotationsToTicks(rotations));
    }

    public void floor(){
        servoGrouping(0.1);
        motorGrouping(0);
    }
    public void highJunction(){
        servoGrouping(0.7);
        motorGrouping(6.9);
    }
    //we might just use v4b arms for this
    public void lowJunction(){
        servoGrouping(0.7);
        motorGrouping(0.5);
    }
    public void midJunction(){
        servoGrouping(0.7);
        motorGrouping(4.9);
    }
}
