# AIBalls
AI learns to control a ball through a maze using neural networks. This simulation is an experiment I conducted to see how a neural network and a genetics algorithm would react in a world bound by physics. Though the physics is abit strange it's not that realistic but it gets the point across.

## Architecture


**Inputs** 

- There are a total of 12 input neurons.

 1. A rayCast in 7 direction at different angles. This helps the AI to make sense of the world it is in. These will help us keep track of the distances between the edges in the world.

 2. The current x & y velocity of the ball.

 3. The height of the nearest wall on the right side of the ball. This can be achieved by casting a ray at 90 degrees towards the nearest edge.

 4. Have checkpoints placed in strategic places and record their x & y coordinates then pick the nearest checkPoint the ball may jump to.


**Hidden**

- There are 4 neurons in this layer.


**Output**

-There are 2 output values in this layer.

 1. The velocity of the ball via x. The ball has the ability to control it's velocity even when it's midAir.

 2. The jump velocity of the ball via y. The ball can only jump if it hit the bottom edge but it can bounce off walls depending on the velocity it wa moving.


## Training

 -The network is trained using a neural evolution using the genetics algorithm.

 1. Population size of 60 agents.
 2. Children are created using the fitness score of the top 50%.<sup>( no cross breeding done)</sup>
 3. Mutation rate of 1% is done to all children for variation.
 4. A direct copy of the top 3 parents are created and passed to the next generation.
 
 # ScreenShot
 
 ![crop1](https://user-images.githubusercontent.com/41951671/186651037-c73b785d-d997-4868-8f44-29101972f3c5.png)

 
 
 

