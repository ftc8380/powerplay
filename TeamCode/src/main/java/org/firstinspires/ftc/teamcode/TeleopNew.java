package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class TeleopNew extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        // For some reason FTC forces you to set target position before RUN_TO_POSITION encoder mode
        DcMotor motorLift = hardwareMap.dcMotor.get("motor lift");
        motorLift.setDirection(DcMotorSimple.Direction.REVERSE);
        motorLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLift.setTargetPosition(0);
        motorLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        DcMotor motorFrontLeft = hardwareMap.dcMotor.get("motor front left");
        DcMotor motorBackLeft = hardwareMap.dcMotor.get("motor back left");
        DcMotor motorFrontRight = hardwareMap.dcMotor.get("motor front right");
        DcMotor motorBackRight = hardwareMap.dcMotor.get("motor back right");
        motorFrontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBackLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        double scale = 0.5; // 1.0 speed is a bit too fast

        // Used for single trigger pull detection
        Gamepad currentGamepad2 = new Gamepad();
        Gamepad previousGamepad2 = new Gamepad();
        Gamepad currentGamepad1 = new Gamepad();
        Gamepad previousGamepad1 = new Gamepad();

        // Empirically tuned for effective grabbing
        double servoMilliseconds = 2750;
        double servoPower = 1.0;
        CRServo servoIntake = hardwareMap.crservo.get("servo intake");
        boolean isLoose = true; // Disallows tightening if already tight and loosening if already loose
        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            // This makes toggle switches possible
            try {
                previousGamepad1.copy(currentGamepad1);
                previousGamepad2.copy(currentGamepad2);
                currentGamepad1.copy(gamepad1);
                currentGamepad2.copy(gamepad2);
            }
            catch (RobotCoreException e) {
                // Swallow any error
            }

            // Slow mode
            if(currentGamepad1.dpad_down && !previousGamepad1.dpad_down) {
                scale = 0.1;
            } else if(currentGamepad1.dpad_left && !previousGamepad1.dpad_left) {
                scale = 0.25;
            } else if(currentGamepad1.dpad_right && !previousGamepad1.dpad_right) {
                scale = 0.5;
            } else if(currentGamepad1.dpad_up && !previousGamepad1.dpad_up) {
                scale = 0.75;
            }

            double y = -gamepad1.left_stick_y; // Why is y still reversed
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = scale * (y + x + rx) / denominator;
            double backLeftPower = scale * (y - x + rx) / denominator;
            double frontRightPower = scale * (y - x - rx) / denominator;
            double backRightPower = scale * (y + x - rx) / denominator;
            motorFrontLeft.setPower(frontLeftPower);
            motorBackLeft.setPower(backLeftPower);
            motorFrontRight.setPower(frontRightPower);
            motorBackRight.setPower(backRightPower);

            // Lift logic
            motorLift.setPower(0.8);
            if (gamepad2.dpad_up) { // Max height
                motorLift.setTargetPosition(rotationsToTicks(6.9));
            } else if (gamepad2.dpad_down) { // Fully retracted
                motorLift.setTargetPosition(0);
            } else if(gamepad2.dpad_left) {
                motorLift.setTargetPosition(rotationsToTicks(0.5));
            } else if(gamepad2.dpad_right) {
                motorLift.setTargetPosition(rotationsToTicks(4.9));
            }

            // LT pulled and claw tight
            if (currentGamepad2.left_trigger > 0.5 && previousGamepad2.left_trigger <= 0.5 && !isLoose) {
                timer.reset();
                while(timer.milliseconds() <= servoMilliseconds) {
                    servoIntake.setPower(servoPower);
                }
                servoIntake.setPower(0);
                isLoose = !isLoose;
            }

            // RT pulled and claw loose
            else if (currentGamepad2.right_trigger > 0.5 && previousGamepad2.right_trigger <= 0.5 && isLoose) {
                timer.reset();
                while(timer.milliseconds() <= servoMilliseconds) {
                    servoIntake.setPower(-servoPower);
                }
                servoIntake.setPower(0);
                isLoose = !isLoose;
            }


        }
    }

    // Used for lift, motor rotations --> encoder ticks
    private int rotationsToTicks(double rotations) {
        double ticksPerRotation = 384.5;
        return (int) (rotations * ticksPerRotation);
    }
}