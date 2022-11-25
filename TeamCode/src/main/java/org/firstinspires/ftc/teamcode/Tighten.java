package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous
@Disabled
public class Tighten extends LinearOpMode {
    public void runOpMode() {
        CRServo servoIntake = hardwareMap.crservo.get("servo intake");
        double servoMilliseconds = 2750;
        double servoPower = 1.0;
        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        waitForStart();

        timer.reset();
        while(timer.milliseconds() <= servoMilliseconds) {
            servoIntake.setPower(-servoPower);
        }
        servoIntake.setPower(0);

        while(timer.seconds() <= 30) {

        }

    }
}
