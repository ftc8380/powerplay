package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class ZeroServo extends LinearOpMode {
    public void runOpMode() {
        Servo s1 = hardwareMap.servo.get("servo left v4b");
        Servo s2 = hardwareMap.servo.get("servo right v4b");
        s1.setDirection(Servo.Direction.REVERSE);
        waitForStart();
        s1.setPosition(0.0);
        s2.setPosition(0.0);
    }
}
