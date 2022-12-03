package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

@TeleOp
@Disabled
public class LiftTest extends LinearOpMode {
    private int rotationsToTicks(double rotations) {
        double ticksPerRotation = 384.5;
        return (int) (rotations * ticksPerRotation);
    }

    public void runOpMode() {

        DcMotor motorLift = hardwareMap.dcMotor.get("motor lift");
        motorLift.setDirection(DcMotorSimple.Direction.REVERSE);
        motorLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorLift.setTargetPosition(0);
        motorLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        waitForStart();

        while(opModeIsActive()) {
            motorLift.setPower(0.8);

            if (gamepad1.dpad_up) {
                motorLift.setTargetPosition(rotationsToTicks(6.5));
            }

            if (gamepad1.dpad_down){
                motorLift.setTargetPosition(0);
            }
        }
    }
}
