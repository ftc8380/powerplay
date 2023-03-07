package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class V4BDown extends LinearOpMode {
    public void runOpMode() {
        DcMotor motorV4b = hardwareMap.dcMotor.get("motor lift");
        Servo servoIntake = hardwareMap.servo.get("servo intake");
        waitForStart();
        servoIntake.setPosition(0.7);
        motorV4b.setPower(0.3);
        sleep(5000);
        motorV4b.setPower(0);
    }
}
