package frc.subsystem.test;

import frc.states.CargoState.IntakeState;
import frc.subsystem.Cargo;

import static frc.utils.Constants.TEST_SHUFFLEBOARD;

public class CargoTest implements SubsystemTest {

    private static final double BREAK_PERIOD = 0.30;
    private Cargo cargo = Cargo.getInstance();
    private double goalTimestamp = 0.0;
    private boolean initializedState = false;
    private CargoTestState state = CargoTestState.INIT_DISABLED;

    public CargoTest() {
        stopIntake();
    }

    private void stopIntake() {
        cargo.setDesiredState(IntakeState.STOPPED);
    }

    private void handleLoop(double timestamp, CargoTestState nextState) {
        if (!initializedState) {
            goalTimestamp = this.state.getTimeToRun() + timestamp;
            initializedState = true;
        }

        if (goalTimestamp + BREAK_PERIOD <= timestamp) {
            initializedState = false;
            state = nextState;
        } else if (goalTimestamp <= timestamp) {
            stopIntake();
        }
    }

    public void outputTelemetry() {
        TEST_SHUFFLEBOARD.putString("Test State", state.toString());
        TEST_SHUFFLEBOARD.putString("Test State Expected", state.getInformation());
        cargo.outputTelemetry();
    }

    public void periodic(double timestamp) {
        switch (state) {
            case INIT_DISABLED:
                handleLoop(timestamp, CargoTestState.INTAKE_FRONT);
                break;
            case INTAKE_FRONT:
                cargo.setDesiredState(IntakeState.INTAKE);
                handleLoop(timestamp, CargoTestState.OUTTAKE_RIGHT);
                break;
            case OUTTAKE_LEFT:
                cargo.setDesiredState(IntakeState.OUTTAKE_LEFT);
                handleLoop(timestamp, CargoTestState.STOPPED);
                break;
            case OUTTAKE_RIGHT:
                cargo.setDesiredState(IntakeState.OUTTAKE_RIGHT);
                handleLoop(timestamp, CargoTestState.OUTTAKE_LEFT);
                break;
            case INTAKE_LEFT:
                cargo.setDesiredState(IntakeState.INTAKE_LEFT);
                handleLoop(timestamp, CargoTestState.OUTTAKE_LEFT);
                break;
            case INTAKE_RIGHT:
                cargo.setDesiredState(IntakeState.INTAKE_RIGHT);
                handleLoop(timestamp, CargoTestState.OUTTAKE_LEFT);
                break;
            case STOPPED:
                cargo.setDesiredState(IntakeState.STOPPED);
                handleLoop(timestamp, CargoTestState.OUTTAKE_LEFT);
                break;
        }
        cargo.writePeriodicOutputs();
    }

    private enum CargoTestState {
        INIT_DISABLED("The robot's intake should not move before setup", 1.0),
        INTAKE_FRONT("The robot should intake, and disable when a ball is introduced", 4.0),
        INTAKE_LEFT("The robot should intake from the left, and disable when a ball is introduced", 4.0),
        INTAKE_RIGHT("The robot should intake from the left, and disable when a ball is introduced", 4.0),
        OUTTAKE_LEFT("The robot should be outtaking to the left", 2.0),
        OUTTAKE_RIGHT("The robot should be outtaking to the right", 2.0),
        STOPPED("The robot's intake should be stopped", 2.0);

        private String information;
        private double timeToRun;

        CargoTestState(String information, double timeToRun) {
            this.information = information;
            this.timeToRun = timeToRun;
        }

        public String getInformation() {
            return information;
        }

        public double getTimeToRun() {
            return timeToRun;
        }
    }
}
