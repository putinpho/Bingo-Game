Coding Style Guide 

1. Naming
    
    
    1.1 - Classes 
        Written in PascalCase
    

        GOOD:

        public class GameBoard

        BAD:

        public class gameBoard
    
    
    1.2 - Methods 
        Written in camelCase
    
    
        GOOD:

        private int getRandomInt(){   
        }

        BAD:

        private int GetRandomInt(){ 
        }

    
    1.3 - Variables and Parameters
        Written in camelCase
   

        Variable names should be clear and concise
        Avoid single character variable names, unless for looping

        GOOD:

        int randUpperBound

        BAD:

        int r 

    
    1.4 - XML Files
        Written in snake_case
    

        GOOD:

        activity_main.XML

        BAD:

        ActivityMain.XML

   
    1.5 - Language
        Written in English
    
        GOOD:

        colour = blue;

        BAD:

        couleur = bleu;

2. Brace Style

    2.1 - Use the standard brace style

        GOOD:

        for(int i = 0; i < 5; i++){
        }

        BAD:

        for(int i = 0; i < 5; i++)
        {  
        }

    2.2 - Braces are used where optional
        Such as in single statement if, else, for, while statements

        GOOD:

        if (newValue){
            return randInt;
        }

        BAD:

        if (newValue)
            return randInt;

3. Declarations

    One declaration per line

    GOOD:

    int randInt;
    int previousRandInt;

    BAD:

    int randInt, previousRandInt;

4. Comments

    Comments should be on all Classes
    Comments should be above unless there is a good reason not to
    Comments should be clear and concise 

    GOOD:

    //This generates a number between 1 and 75
    //This function also ensures there will be no duplicates

    private int getRandomInt(int[] dupArray){
    }

    BAD:

    int number = 5; // Assigns the variable number the value 5


5. Formatting

    5.1 Whitespace

        5.1.1 Vertical Whitespace
        
            Add single blank lines, or multiple to improve readibility

        5.1.2 Horizontal Whitespace

            Single spaces should be added in the following situations:

            Separating expressions in if statements, and loops.

                GOOD: 
            
                for(int i = 0; i < 5; i++){
                }

                BAD:

                for(int i=0;i<5;i++){ 
                }

        






    
    
