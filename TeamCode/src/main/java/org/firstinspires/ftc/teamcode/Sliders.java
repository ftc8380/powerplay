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


//also we'll change the values accordingly, these values are placeholders

public class Sliders {
    Servo servoLeftV4b;
    Servo servoRightV4b;
    DcMotor motorLiftLeft;
    DcMotor motorLiftRight;
    Servo servoIntake;

    public Sliders(HardwareMap hm) {
        motorLiftLeft = hm.dcMotor.get("motor lift left");
        motorLiftRight = hm.dcMotor.get("motor lift right");
        servoLeftV4b = hm.servo.get("servo left v4b");
        servoRightV4b = hm.servo.get("servo right v4b");
        servoIntake = hm.servo.get("servo intake");
        motorLiftRight.setDirection(DcMotorSimple.Direction.REVERSE);
        motorLiftLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLiftRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLiftLeft.setTargetPosition(0);
        motorLiftRight.setTargetPosition(0);
        motorLiftLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorLiftRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        servoRightV4b.setDirection(Servo.Direction.REVERSE);
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
        closeClaw();
        servoGrouping(0);
        motorGrouping(0.3);
    }
    public void highJunction(){
        closeClaw();
        servoGrouping(0.6);
        motorGrouping(3.5);
    }
    //we might just use v4b arms for this
    public void lowJunction(){
        closeClaw();
        servoGrouping(0);
        motorGrouping(0.3);
    }
    public void midJunction(){
        closeClaw();
        servoGrouping(0.6);
        motorGrouping(1.5);
    }
}
