package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class IntakeTest extends LinearOpMode {
    public void runOpMode() {
        CRServo servoIntake = hardwareMap.crservo.get("servo intake");

        Gamepad currentGamepad1 = new Gamepad();
        Gamepad previousGamepad1 = new Gamepad();

        double maxMS = 2250;
        double power = 1.0;

        boolean isLoose = true;
        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        waitForStart();

        while (opModeIsActive()) {
            try {
                previousGamepad1.copy(currentGamepad1);
                currentGamepad1.copy(gamepad1);
            }
            catch (RobotCoreException e) {
                // Swallow the possible exception, it should not happen as
                // currentGamepad1/2 are being copied from valid Gamepads.
            }

            // Main teleop loop goes here

            if (currentGamepad1.left_trigger > 0.5 && previousGamepad1.left_trigger <= 0.5 && !isLoose) {
                timer.reset();
                while(timer.milliseconds() <= maxMS) {
                    servoIntake.setPower(power);
                }
                servoIntake.setPower(0);
                isLoose = !isLoose;
            }

            if (currentGamepad1.right_trigger > 0.5 && previousGamepad1.right_trigger <= 0.5 && isLoose) {
                timer.reset();
                while(timer.milliseconds() <= maxMS) {
                    servoIntake.setPower(-power);
                }
                servoIntake.setPower(0);
                isLoose = !isLoose;
            }


        }
    }
}