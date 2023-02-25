package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
@Config
public class IntakeTest extends LinearOpMode {
    public static double INTAKE_POS = 0.0;
    public void runOpMode() {
        Servo servoIntake = hardwareMap.servo.get("servo intake");
        waitForStart();
        while(opModeIsActive()) {
            servoIntake.setPosition(INTAKE_POS);
        }
    }
}
