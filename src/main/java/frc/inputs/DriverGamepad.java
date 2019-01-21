package frc.inputs;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import frc.utils.DriveSignal;

import static frc.utils.Constants.DRIVER_GAMEPAD_PORT;

public class DriverGamepad implements DriverHid {
    private static DriverGamepad instance;
    private XboxController gamepad;

    private DriverGamepad() {
        gamepad = new XboxController(DRIVER_GAMEPAD_PORT);
    }

    public static DriverGamepad getInstance() {
        if (instance == null) {
            instance = new DriverGamepad();
        }
        return instance;
    }

    @Override
    public DriveSignal getDriveSignal() {
        return new DriveSignal(gamepad.getY(GenericHID.Hand.kLeft), gamepad.getY(GenericHID.Hand.kRight));
    }
}