package frc.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.subsystem.DriveTrain;
import frc.subsystem.Elevator;
import frc.subsystem.ElevatorPosition;
import frc.subsystem.Hatch;
import frc.utils.DriveSignal;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

import java.io.File;

public class Lvl2RightCloseRKT2 extends AutoBase {
    private final DriveTrain drive = DriveTrain.getInstance();
    private final Hatch hatch = Hatch.getInstance();
    private final Elevator elevator = Elevator.getInstance();
    private State state = State.INIT;

    public static Trajectory loadTrajectory(String path) {
        return Pathfinder.readFromCSV(new File(path));
    }

    @Override
    public void init() {

    }

    @Override
    public void periodic(double timestamp) {
        SmartDashboard.putString("Autostate", state.toString());
        switch (state) {
            case INIT:
                System.out.println(state);
                state = State.LEVEL2_to_Rocket;
                drive.setTrajectory(loadTrajectory("/home/lvuser/deploy/paths/LEVEL2_to_Rocket.pf1.csv"), false);
                elevator.pidToPosition(ElevatorPosition.HATCH_MID);
                System.out.println(state);
                break;
            case LEVEL2_to_Rocket:
                hatch.setHatchPlace();
                if (drive.isDone()) {
                    state = State.Right_RKT_Close_Backup;
                    System.out.println(state);
                    drive.setTrajectory(loadTrajectory("/home/lvuser/deploy/paths/Right_RKT_Close_Backup.pf1.csv"), true);
                }
                break;
            case Right_RKT_Close_Backup:
                hatch.setHatchPull();
                elevator.pidToPosition(ElevatorPosition.HATCH_LOW);
                if (drive.isDone()) {
                    state = State.Close_Right_Rkt_to_FEED;
                    System.out.println(state);
                    drive.setTrajectory(loadTrajectory("/home/lvuser/deploy/paths/Close_Right_Rkt_to_FEED.pf1.csv"), false);

                }
                break;
            //180 degree turn
            case Close_Right_Rkt_to_FEED:
                if (drive.isDone()) {
                    state = State.STOP;
                    System.out.println(state);
                }
                break;
            case STOP:
                System.out.println(state);
//                elevator.pidToPosition(Elevator.ElevatorPosition.HATCH_LOW);
                drive.setOpenLoop(DriveSignal.NEUTRAL);
                break;
        }
    }

    @Override
    public boolean isDone() {
        return state == State.STOP;
    }

    private enum State {
        INIT,
        LEVEL2_to_Rocket,
        Right_RKT_Close_Backup,
        Close_Right_Rkt_to_FEED,
        STOP
    }
}
