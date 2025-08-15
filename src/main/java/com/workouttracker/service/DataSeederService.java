package com.workouttracker.service;

import com.workouttracker.model.Exercise;
import com.workouttracker.repository.ExerciseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Order(1) // Ensure this runs early in the application startup
public class DataSeederService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeederService.class);

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Override
    @Transactional
    public void run(String... args) {
        try {
            if (exerciseRepository.count() == 0) {
                logger.info("Database is empty. Starting exercise data seeding...");
                seedExercises();
                logger.info("Exercise data seeding completed successfully!");
            } else {
                logger.info("Exercise data already exists. Skipping seeding.");
            }
        } catch (Exception e) {
            logger.error("Error occurred during data seeding: ", e);
        }
    }

    private void seedExercises() {
        // Seed all categories
        seedStrengthTrainingExercises();
        seedCardioExercises();
        seedFlexibilityExercises();
        seedSportsSpecificExercises();
        seedFunctionalFitnessExercises();
        seedRehabilitationExercises();
    }

    private void seedStrengthTrainingExercises() {
        logger.info("Seeding strength training exercises...");

        List<Exercise> strengthExercises = Arrays.asList(
            // CHEST
            createExercise("Barbell Bench Press", "Lie on bench, lower barbell to chest, press up explosively", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CHEST, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Incline Barbell Press", "Bench press on inclined bench targeting upper chest", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CHEST, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Dumbbell Bench Press", "Bench press using dumbbells for greater range of motion", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CHEST, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Incline Dumbbell Press", "Dumbbell press on inclined bench", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CHEST, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Push-ups", "Basic bodyweight chest exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CHEST, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Diamond Push-ups", "Push-ups with hands forming diamond shape", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CHEST, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Wide-Grip Push-ups", "Push-ups with hands wider than shoulders", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CHEST, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Dips", "Bodyweight exercise using parallel bars", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CHEST, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Chest Fly", "Dumbbell fly exercise for chest isolation", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CHEST, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Cable Crossover", "Cable machine chest exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CHEST, Exercise.DifficultyLevel.INTERMEDIATE),

            // BACK
            createExercise("Deadlift", "Hip hinge movement lifting weight from floor", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.ADVANCED),
            createExercise("Sumo Deadlift", "Deadlift with wide stance", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.ADVANCED),
            createExercise("Romanian Deadlift", "Hip hinge deadlift variation", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Pull-ups", "Bodyweight exercise pulling body up to bar", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Chin-ups", "Pull-ups with supinated grip", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Lat Pulldown", "Cable machine lat exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Barbell Rows", "Bent-over rowing with barbell", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Dumbbell Rows", "Single-arm rowing with dumbbell", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.BEGINNER),
            createExercise("T-Bar Row", "Rowing exercise using T-bar", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Cable Rows", "Seated cable rowing exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.BEGINNER),

            // LEGS
            createExercise("Back Squat", "Barbell squat with bar on upper back", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Front Squat", "Squat with barbell held in front", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.ADVANCED),
            createExercise("Goblet Squat", "Squat holding dumbbell at chest", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Bulgarian Split Squat", "Single-leg squat with rear foot elevated", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Walking Lunges", "Alternating forward lunges", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Reverse Lunges", "Stepping backward into lunge position", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Side Lunges", "Lateral lunge movement", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Leg Press", "Machine-based leg pressing exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Leg Extension", "Isolated quadriceps exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Leg Curl", "Isolated hamstring exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Calf Raises", "Standing calf muscle exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Wall Sit", "Isometric quad exercise against wall", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),

            // SHOULDERS
            createExercise("Overhead Press", "Standing barbell press overhead", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Dumbbell Shoulder Press", "Seated or standing dumbbell press", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Arnold Press", "Rotating dumbbell shoulder press", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Lateral Raises", "Side dumbbell raises for medial delts", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Front Raises", "Forward dumbbell raises", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Rear Delt Flyes", "Reverse fly for posterior deltoids", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Pike Push-ups", "Bodyweight shoulder exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Handstand Push-ups", "Advanced bodyweight shoulder exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.ADVANCED),
            createExercise("Upright Rows", "Pulling weight to chest level", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Face Pulls", "Cable exercise for rear delts", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.BEGINNER),

            // ARMS
            createExercise("Barbell Curls", "Standing bicep curls with barbell", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.ARMS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Dumbbell Curls", "Bicep curls with dumbbells", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.ARMS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Hammer Curls", "Neutral grip dumbbell curls", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.ARMS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Preacher Curls", "Bicep curls on preacher bench", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.ARMS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Close-Grip Bench Press", "Bench press with narrow grip for triceps", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.ARMS, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Tricep Dips", "Bodyweight tricep exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.ARMS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Overhead Tricep Extension", "Tricep extension with weight overhead", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.ARMS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Tricep Pushdowns", "Cable machine tricep exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.ARMS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("21s", "Bicep curl variation with partial reps", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.ARMS, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Skull Crushers", "Lying tricep extension", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.ARMS, Exercise.DifficultyLevel.INTERMEDIATE),

            // CORE
            createExercise("Plank", "Isometric core strengthening exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CORE, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Side Plank", "Lateral plank for obliques", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CORE, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Crunches", "Basic abdominal exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CORE, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Bicycle Crunches", "Alternating crunch with knee to elbow", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CORE, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Russian Twists", "Seated torso rotation exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CORE, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Mountain Climbers", "Dynamic core and cardio exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CORE, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Dead Bug", "Core stability exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CORE, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Bird Dog", "Core and back stability exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CORE, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Hanging Knee Raises", "Hanging abdominal exercise", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CORE, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Ab Wheel Rollout", "Advanced core exercise with ab wheel", 
                Exercise.Category.STRENGTH_TRAINING, Exercise.MuscleGroup.CORE, Exercise.DifficultyLevel.ADVANCED)
        );

        exerciseRepository.saveAll(strengthExercises);
        logger.info("Seeded {} strength training exercises", strengthExercises.size());
    }

    private void seedCardioExercises() {
        logger.info("Seeding cardio exercises...");

        List<Exercise> cardioExercises = Arrays.asList(
            // RUNNING/JOGGING
            createExercise("Running", "Continuous running at steady pace", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Interval Running", "Alternating high and low intensity running", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Sprint Intervals", "High intensity sprint intervals", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.ADVANCED),
            createExercise("Hill Sprints", "Sprint running uphill", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.ADVANCED),
            createExercise("Treadmill Running", "Indoor running on treadmill", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),

            // CYCLING
            createExercise("Stationary Bike", "Indoor cycling on stationary bike", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Road Cycling", "Outdoor cycling on roads", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Spin Class", "High-intensity indoor cycling class", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Mountain Biking", "Off-road cycling", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.INTERMEDIATE),

            // SWIMMING
            createExercise("Swimming Freestyle", "Front crawl swimming stroke", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Swimming Backstroke", "Swimming on back", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Swimming Breaststroke", "Breast stroke swimming", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Water Jogging", "Running in deep water", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),

            // HIGH INTENSITY
            createExercise("Burpees", "Full body high-intensity exercise", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Jumping Jacks", "Jump with arms and legs spreading", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("High Knees", "Running in place with high knee lift", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Butt Kickers", "Running in place kicking heels to glutes", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Jump Rope", "Cardio exercise with jumping rope", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Box Jumps", "Jumping onto elevated platform", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.INTERMEDIATE),

            // ROWING/ELLIPTICAL
            createExercise("Rowing Machine", "Full body rowing exercise", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Elliptical", "Low-impact elliptical machine cardio", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Stair Climber", "Stair climbing machine", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.INTERMEDIATE),

            // DANCING/AEROBICS
            createExercise("Aerobics", "Group aerobic exercise class", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Zumba", "Dance-based fitness program", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Step Aerobics", "Aerobics using step platform", 
                Exercise.Category.CARDIO, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.INTERMEDIATE)
        );

        exerciseRepository.saveAll(cardioExercises);
        logger.info("Seeded {} cardio exercises", cardioExercises.size());
    }

    private void seedFlexibilityExercises() {
        logger.info("Seeding flexibility exercises...");

        List<Exercise> flexibilityExercises = Arrays.asList(
            // STATIC STRETCHES
            createExercise("Hamstring Stretch", "Seated or standing hamstring flexibility", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Quad Stretch", "Standing quadriceps stretch", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Calf Stretch", "Wall or standing calf stretch", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Hip Flexor Stretch", "Lunge position hip flexor stretch", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Chest Stretch", "Doorway or wall chest stretch", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.CHEST, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Shoulder Stretch", "Cross-body shoulder stretch", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Tricep Stretch", "Overhead tricep stretch", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.ARMS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Neck Stretch", "Side neck stretch", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),

            // YOGA POSES
            createExercise("Downward Dog", "Inverted V-shape yoga pose", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Child's Pose", "Kneeling rest pose", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Cat-Cow Stretch", "Spinal mobility exercise", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Cobra Pose", "Back extension yoga pose", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Pigeon Pose", "Hip opening yoga pose", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Warrior I", "Standing yoga pose", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Warrior II", "Side warrior yoga pose", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Triangle Pose", "Standing side stretch", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),

            // DYNAMIC STRETCHES
            createExercise("Leg Swings", "Dynamic leg mobility", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Arm Circles", "Dynamic shoulder mobility", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Hip Circles", "Dynamic hip mobility", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Torso Twists", "Dynamic spinal rotation", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.CORE, Exercise.DifficultyLevel.BEGINNER),

            // FOAM ROLLING
            createExercise("Foam Roll Quads", "Quadriceps foam rolling", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Foam Roll IT Band", "IT band foam rolling", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Foam Roll Calves", "Calf muscle foam rolling", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Foam Roll Back", "Upper back foam rolling", 
                Exercise.Category.FLEXIBILITY, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.BEGINNER)
        );

        exerciseRepository.saveAll(flexibilityExercises);
        logger.info("Seeded {} flexibility exercises", flexibilityExercises.size());
    }

    private void seedSportsSpecificExercises() {
        logger.info("Seeding sports-specific exercises...");

        List<Exercise> sportsExercises = Arrays.asList(
            // BASKETBALL
            createExercise("Basketball Shooting", "Practice shooting baskets", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.ARMS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Basketball Dribbling", "Ball handling drills", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Defensive Slides", "Lateral defensive movement", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.INTERMEDIATE),

            // SOCCER
            createExercise("Soccer Ball Juggling", "Keeping ball airborne with feet", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Soccer Shooting", "Goal shooting practice", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Soccer Passing", "Ball passing drills", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),

            // TENNIS
            createExercise("Tennis Forehand", "Forehand stroke practice", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.ARMS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Tennis Backhand", "Backhand stroke practice", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.ARMS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Tennis Serve", "Serving practice", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.INTERMEDIATE),

            // GOLF
            createExercise("Golf Swing Practice", "Golf swing mechanics", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Golf Putting", "Short game putting practice", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.ARMS, Exercise.DifficultyLevel.BEGINNER),

            // BASEBALL/SOFTBALL
            createExercise("Baseball Throwing", "Throwing mechanics practice", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Baseball Batting", "Batting practice swings", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),

            // SWIMMING SPECIFIC
            createExercise("Swimming Drills", "Stroke technique drills", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Kick Board", "Swimming kick practice", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),

            // MARTIAL ARTS
            createExercise("Shadow Boxing", "Boxing movements without opponent", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Karate Forms", "Martial arts kata practice", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Taekwondo Kicks", "High kick practice", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.INTERMEDIATE),

            // TRACK AND FIELD
            createExercise("Hurdle Drills", "Hurdle technique practice", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.ADVANCED),
            createExercise("Long Jump Practice", "Long jump technique", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Shot Put", "Shot put throwing practice", 
                Exercise.Category.SPORTS_SPECIFIC, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.ADVANCED)
        );

        exerciseRepository.saveAll(sportsExercises);
        logger.info("Seeded {} sports-specific exercises", sportsExercises.size());
    }

    private void seedFunctionalFitnessExercises() {
        logger.info("Seeding functional fitness exercises...");

        List<Exercise> functionalExercises = Arrays.asList(
            // CROSSFIT MOVEMENTS
            createExercise("Thrusters", "Squat to overhead press movement", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Wall Balls", "Squat and throw medicine ball to wall", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Kettlebell Swings", "Hip hinge kettlebell movement", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Turkish Get-ups", "Complex full-body movement", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.ADVANCED),
            createExercise("Clean and Jerk", "Olympic lifting movement", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.ADVANCED),
            createExercise("Snatch", "Olympic lifting overhead movement", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.ADVANCED),

            // COMPOUND MOVEMENTS
            createExercise("Farmer's Walk", "Walking with heavy weights", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Sled Push", "Pushing weighted sled", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Tire Flips", "Flipping large tire", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.ADVANCED),
            createExercise("Battle Ropes", "Rope wave exercises", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.INTERMEDIATE),

            // BODYWEIGHT FUNCTIONAL
            createExercise("Bear Crawl", "Crawling movement on hands and feet", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Crab Walk", "Walking in crab position", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Duck Walk", "Walking in squat position", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("Lizard Crawl", "Low crawling movement", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.INTERMEDIATE),

            // KETTLEBELL SPECIFIC
            createExercise("Kettlebell Goblet Squat", "Squat holding kettlebell", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Kettlebell Windmill", "Overhead reach and touch floor", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.CORE, Exercise.DifficultyLevel.ADVANCED),
            createExercise("Kettlebell Halos", "Circling kettlebell around head", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.INTERMEDIATE),

            // SUSPENSION TRAINING
            createExercise("TRX Suspension Squat", "Squat using suspension trainer", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("TRX Push-up", "Push-up using suspension trainer", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.CHEST, Exercise.DifficultyLevel.INTERMEDIATE),
            createExercise("TRX Row", "Rowing using suspension trainer", 
                Exercise.Category.FUNCTIONAL_FITNESS, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.BEGINNER)
        );

        exerciseRepository.saveAll(functionalExercises);
        logger.info("Seeded {} functional fitness exercises", functionalExercises.size());
    }

    private void seedRehabilitationExercises() {
        logger.info("Seeding rehabilitation exercises...");

        List<Exercise> rehabExercises = Arrays.asList(
            // PHYSICAL THERAPY
            createExercise("Glute Bridge", "Hip strengthening exercise", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Clamshells", "Hip abduction exercise", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Wall Angels", "Shoulder mobility exercise", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Ankle Circles", "Ankle mobility exercise", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),

            // CORE STABILITY
            createExercise("Modified Plank", "Easier plank variation", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.CORE, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Pelvic Tilts", "Lower back and core exercise", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.CORE, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Knee to Chest", "Lower back stretch", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.BEGINNER),

            // BALANCE AND PROPRIOCEPTION
            createExercise("Single Leg Stand", "Balance training exercise", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Heel-to-Toe Walk", "Balance and coordination", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Bosu Ball Balance", "Unstable surface balance", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.INTERMEDIATE),

            // RESISTANCE BAND EXERCISES
            createExercise("Band Pull-Aparts", "Resistance band chest exercise", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.CHEST, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Band External Rotation", "Shoulder rehabilitation", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Band Leg Raises", "Hip strengthening with resistance", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),

            // GENTLE MOVEMENTS
            createExercise("Seated Marching", "Seated leg movement", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Arm Raises Seated", "Seated arm strengthening", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.SHOULDERS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Gentle Spinal Twist", "Seated spinal mobility", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.BACK, Exercise.DifficultyLevel.BEGINNER),

            // POST-INJURY
            createExercise("Isometric Quad", "Quadriceps strengthening without movement", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Towel Calf Stretch", "Gentle calf flexibility", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.LEGS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Wrist Circles", "Wrist mobility exercise", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.ARMS, Exercise.DifficultyLevel.BEGINNER),
            createExercise("Neck Range of Motion", "Gentle neck mobility", 
                Exercise.Category.REHABILITATION, Exercise.MuscleGroup.FULL_BODY, Exercise.DifficultyLevel.BEGINNER)
        );

        exerciseRepository.saveAll(rehabExercises);
        logger.info("Seeded {} rehabilitation exercises", rehabExercises.size());
    }

    private Exercise createExercise(String name, String description, Exercise.Category category, 
                                  Exercise.MuscleGroup primaryMuscleGroup, Exercise.DifficultyLevel difficulty) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setDescription(description);
        exercise.setCategory(category);
        exercise.setPrimaryMuscleGroup(primaryMuscleGroup);
        exercise.setDifficultyLevel(difficulty);
        exercise.setActive(true);
        return exercise;
    }
}
