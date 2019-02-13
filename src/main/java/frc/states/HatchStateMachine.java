package frc.states;

import com.ctre.phoenix.motorcontrol.ControlMode;

import static frc.states.HatchState.ControlState.*;

public class HatchStateMachine {
    public static final double PEAK_FORWARD_OUTPUT_STANDARD = 1.0;
    public static final double PEAK_REVERSE_OUTPUT_STANDARD = -1.0;
    private static final double PEAK_FORWARD_OUTPUT_LIMIT_PRESSED = 1.0;
    private static final double PEAK_REVERSE_OUTPUT_LIMIT_PRESSED = 0.0;

    private HatchState systemState = new HatchState();
    private HatchState.ControlState desiredControlState = STOPPED;
    private int desiredEncoderPosition = 0;
    private double desiredOpenLoopPower = 0;
    private boolean limitHitOnLastUpdate = false;

    public HatchStateMachine() {

    }

    public synchronized void setPosition(double joystickPower) {
        this.desiredOpenLoopPower = 0.0;
        this.desiredControlState = MOTION_MAGIC;
        this.desiredEncoderPosition = 500;
    }

    public synchronized void setOpenLoop(double joystickPower) {
        this.desiredControlState = OPEN_LOOP;
        this.desiredOpenLoopPower = joystickPower;
    }

    public synchronized HatchState update(HatchState currentState) {
        if (currentState.limitHit) {
            systemState.peakOutputForward = PEAK_FORWARD_OUTPUT_LIMIT_PRESSED;
            systemState.peakOutputReverse = PEAK_REVERSE_OUTPUT_LIMIT_PRESSED;
            if (!limitHitOnLastUpdate && !systemState.hasZeroed) {
                systemState.resetSensor = true;
                systemState.hasZeroed = true;
            }
        } else {
            systemState.resetSensor = false;
            systemState.peakOutputForward = PEAK_FORWARD_OUTPUT_STANDARD;
            systemState.peakOutputReverse = PEAK_REVERSE_OUTPUT_STANDARD;
        }

        limitHitOnLastUpdate = currentState.limitHit;

        // If we haven't zeroed, the current state requires zeroing, and we haven't hit the limit, zero the mechanism
        if (!systemState.hasZeroed && desiredControlState.requiresZeroing()) {
            systemState.hatchState = ZEROING;
        } else {
            systemState.hatchState = desiredControlState;
        }

        handleUpdate();

        return systemState;
    }

    private synchronized void handleUpdate() {
        switch (systemState.hatchState) {
            case ZEROING:
                systemState.demand = systemState.encoder + 30 /* + TODO */;
                systemState.controlMode = ControlMode.MotionMagic;
                break;
            case MOTION_MAGIC:
                systemState.demand = desiredEncoderPosition;
                systemState.controlMode = ControlMode.MotionMagic;
                break;
            case OPEN_LOOP:
                systemState.demand = desiredOpenLoopPower;
                systemState.controlMode = ControlMode.PercentOutput;
                break;
            case STOPPED:
                systemState.demand = 0.0;
                systemState.controlMode = ControlMode.PercentOutput;
                break;
        }
    }
}
