package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


//also we'll change the values accordingly, these values are placeholders

public class Sliders {
    DcMotor motorLiftLeft;
    DcMotor motorLiftRight;
    Servo servoIntake;
    DcMotor motorV4b;

    public Sliders(HardwareMap hm) {
        motorLiftLeft = hm.dcMotor.get("motor lift left");
        motorLiftRight = hm.dcMotor.get("motor lift right");
        servoIntake = hm.servo.get("servo intake");
        motorLiftRight.setDirection(DcMotorSimple.Direction.REVERSE);
        motorLiftLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLiftRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLiftLeft.setTargetPosition(0);
        motorLiftRight.setTargetPosition(0);
        motorLiftLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorLiftRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        motorV4b = hm.dcMotor.get("motor lift");
        motorV4b.setDirection(DcMotor.Direction.REVERSE);
        motorV4b.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorV4b.setTargetPosition(0);
        motorV4b.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    private int rotationsToTicks(double rotations) {
        double ticksPerRotation = 384.5;
        return (int) (rotations * ticksPerRotation);
    }

    public void openClaw() {
        servoIntake.setPosition(1.0);
    }

    public void closeClaw() {
        servoIntake.setPosition(0.5);
    }

    public void motorV4bUp() {
        motorV4b.setPower(-0.5);
        motorV4b.setTargetPosition(2700);
    }

    public void motorV4bDown() {
        motorV4b.setPower(-0.5);
        motorV4b.setTargetPosition(0);
    }

    public void motorGrouping(double rotations){
        motorLiftLeft.setPower(0.5);
        motorLiftRight.setPower(0.5);
        motorLiftLeft.setTargetPosition(rotationsToTicks(rotations));
        motorLiftRight.setTargetPosition(rotationsToTicks(rotations));
    }

    public void floor(){
        closeClaw();
//        servoGrouping(0);
        motorGrouping(0.3);
        //motorV4bDown();
    }
    public void highJunction(){
       closeClaw();
//        servoGrouping(0.8);
        motorGrouping(3.5);
       // motorV4bUp();
    }
    //we might just use v4b arms for this
    public void lowJunction(){
      closeClaw();
//        servoGrouping(0);
        motorGrouping(0.3);
        //motorV4bDown();
    }
    public void midJunction(){
//        closeClaw();
//        servoGrouping(0.8);
        motorGrouping(1.5);
        //motorV4bUp();
    }
}
