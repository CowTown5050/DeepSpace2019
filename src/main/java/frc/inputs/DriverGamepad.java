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

    @Override
    public boolean liftJack() {
        return gamepad.getAButton();
    }

    // TODO

    @Override
    public boolean extendFrontJack() {
        return false;
    }

    @Override
    public boolean retractFrontJack() {
        return false;
    }

    @Override
    public boolean extendLeftJack() {
        return false;
    }

    @Override
    public boolean retractLeftJack() {
        return false;
    }

    @Override
    public boolean extendRightJack() {
        return false;
    }

    @Override
    public boolean retractRightJack() {
        return false;
    }

    @Override
    public DriveSignal runJackWheels() {
        return null;
    }

    @Override
    public boolean holdAll() {
        return false;
    }
}
