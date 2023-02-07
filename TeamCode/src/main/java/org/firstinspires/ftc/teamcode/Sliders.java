package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


//also we'll change the values accordingly, these values are placeholders

public class Sliders {
    DcMotor motorLiftLeft;
    DcMotor motorLiftRight;


    public Sliders() {
        motorLiftLeft = hardwareMap.dcMotor.get("motor lift left");
        motorLiftRight = hardwareMap.dcMotor.get("motor lift right");
    }

    private int rotationsToTicks(double rotations) {
        double ticksPerRotation = 384.5;
        return (int) (rotations * ticksPerRotation);
    }

    private void motorGrouping(double rotations){
        motorLiftLeft.setTargetPosition(rotationsToTicks(rotations));
        motorLiftRight.setTargetPosition(rotationsToTicks(rotations));
    }

    public void floor(){
        motorGrouping(0);
    }
    public void highJunction(){
        motorGrouping(6.9);
    }
    //we might just use v4b arms for this
    public void lowJunction(){
        motorGrouping(0.5);
    }
    public void midJunction(){
        motorGrouping(4.9);
    }
}
