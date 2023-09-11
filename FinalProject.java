package demo;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
public class FinalProject extends JFrame {
    public FinalProject() {

        Game tetris2048 = new Game();
        addKeyListener(tetris2048.listener);//Register to listener
        add(tetris2048);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(580, 250);
        setTitle("Tetris&2048");
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new FinalProject();
    }
}

// Create game class
class Game extends JPanel {
    public TimerListener listener = new TimerListener();
    private int score = 0;//Record scores

   Vector<Block> blocks=new Vector<Block>();//Used to store blocks

    int num = -1;//Used to identify the total number of blocks

    int size=20;//Side length of block

    int flag=0;//Used to identify the process status of the game, victory, failure, or in the game

    // Initialize construction method
    public Game() {
        nextBlock();//Add block
        new Timer(1000, listener).start();//The falling speed of a block is one unit per second
    }

    // The method of block moving left
    private void left() {
        Block block=blocks.get(num);
      if(block.getX()>size&&flag==0){//When the box is in range and the game is still in progress
          if(judge(block,"left")==1){//If there is no block to the left of the current block
              block.setX(block.getX()-size);//Move one unit left
          }
      }
        repaint();//Repaint interface
    }

    // The method of block moving right
    private void right() {
        Block block = blocks.get(num);
        if (block.getX()<size*6&&flag==0) {//When the box is in range and the game is still in progress
            if(judge(block,"right")==1){//If there is no block to the right of the current block
                block.setX(block.getX()+size);//Move one unit right
            }
            repaint();//Repaint interface
        }
    }

    //The method of judging whether there is a block on the left and right of the current block. 0 is yes, 1 is no
    private int judge(Block block,String direction) {
    	for(int i=0;i<blocks.size()&&i!=num;i++) {
    		Block block1=blocks.get(i);
            	if(direction.equals("right")) {//Judge right
            		if(block.getX()==block1.getX()-size&&block1.getY()==block.getY()){
                        return 0;
                    }
                }else if(direction.equals("left")) {//Judge left
                    if(block.getX()==block1.getX()+size&&block1.getY()==block.getY()){

                        return 0;
                    }
                }
            }
            return 1;
        }
    
    //Method of block falling
    private void down() {
    	if(flag==0) {            
    		Block block=blocks.get(num);
    			if(block.getY()<size*8) {//When the block does not reach the bottom
    				switch (crash(block)) {
                   	case -1: nextBlock();
                   	break;
                    case 0: nextBlock();
                    break;
                    case 1: nextBlock();
                    break;
                    case 2: block.setY(block.getY()+size);
                    break;
                }
            }else{//When the block reaches the bottom
                nextBlock();
            }
            repaint();
        }
    }

    //The method of judge whether the blocks crash
    private int crash(Block block) {
       int x=block.getX();
       int y=block.getY();

       for(int k=0; k<blocks.size()&&k!=num; k++) {
    	   Block block1=blocks.get(k);
   		   if(block1.getX()==x&&block1.getY()==y+size){//When there is a block under the current block    			   
   			   if(block.getY()==0){//If the current blcok is just at the top, the game is over    				   
   				   flag=-1;//The game is over
   				   break;
    			   }else{                    
    			    if(block.getValue()==block1.getValue()){//When the values of the upper and lower blocks are the same
    				   blocks.remove(block);//Remove the upper block
    				   num--;
    				   block1.setValue(block1.getValue()*2);//The value of the lower block has doubled

                       if(block1.getValue()==64){//When the mixed block value is 64
                    	   blocks.remove(block1);//Remove 64 block
                           num--;
                           score += 1;
                           return -1;//If the two values are the same and the value of block reaches 64, return -1
                        }else{
                        	
                          Block block2=judge(block1);
                            if (block1!=block2){
                            	Block block3 = judge(block2);
                                 if (block2 != block3) {
                              		 Block block4 = judge(block3);
                                       if (block4 != block3) {
                                    	   Block block5=judge(block4);
                                    }
                                }
                            }
                            return 0;//If the two values are the same, but the value of the block doesn't reach 64, return 0
                        }
                    }
                }

                return 1;//If there is a block below the current block one unit, but other conditions are not met, return 1
            }
        }

        return 2;//If there is no block below the current block, return 2
    }

    //The method used to judge whether the mixed blocks can continue to mix the block below
    private Block judge(Block block) {    	   
    	for(int k=0;k<blocks.size()&&k!=num;k++) {
    		Block block1=blocks.get(k);
    		if(block1.getX()==block.getX()&&block1.getY()==block.getY()+size) {

    			if (block.getValue() == block1.getValue()) {
                    blocks.remove(block);
                    num--;
                    block1.setValue(block1.getValue() * 2);
                    if (block1.getValue() == 64) {
                        blocks.remove(block1);
                        num--;
                        score += 1;
                    }else{
                    	return block1;
                    	}
                  	}
               	}
          	}
    	  return block;
       }

    //The method of adding a new block
    private void nextBlock() {
       num++;
       Block block =new Block();
       block.setX(size);//Set the starting position, which is in the upper left corner by default

       int ran= (int) (Math.random()*100);//Probability of generating blocks of different values
       int value=0;
       if(ran<55) {
           value=2;//The probability of adding a 2 block is 55%
       }else if(ran<78) {
           value=4;//The probability of adding a 4 block is 23%
        }else if(ran<90) {
           value=8;//The probability of adding a 8 block is 12%
       }else if(ran<98) {
           value=16;//The probability of adding a 16 block is 8%
       }else{
           value=32;//The probability of adding a 32 block is 2%
       }
       block.setValue(value);

        if(flag==0) {//If the game is in progress
            blocks.add(block);//Continue to add blocks
        }
    }

    //The method of showing the words at the end of the game
    public void paintComponent(Graphics g) {
        if(score==20) {
            flag=1;//When the score reaches 20, return 1
        }
        super.paintComponent(g);
        if(flag==-1) {//If the game is over, the following word will appear
            g.setFont(new Font("Arial",Font.BOLD,40));
            g.drawString("Game Over!",50,120);
        }else if(flag==1) {//If the game is win, the following word will appear
            g.setFont(new Font("Arial",Font.BOLD,40));
            g.drawString("Victory",50,120);
        }

        //Draw the current block
        for(int k=0;k<blocks.size();k++) {
            Block block=blocks.get(k);
            g.setColor(Color.GRAY);
            g.fillRect(block.getX(),block.getY(),size,size);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial",Font.BOLD,14));
            g.drawString(block.getValue()+"",block.getX()+5,block.getY()+size-2);
        }
        
        //Draw the boundaries of the game
        for (int j = 0; j < 10; j++) {
            g.setColor(Color.BLACK);
            g.drawRect(0, j * size, size, size);
            g.drawRect(size*7, j * size, size, size);
                }
        for(int i=0;i<7;i++) {
            g.drawRect(i*size,size*9,size,size);
        }

        //Introduction
        g.drawString("score=" + score, 165, 30);
        g.setFont(new Font("Arial",Font.BOLD,12));
        g.drawString("Operating instructions:",165,90);
        g.drawString("You can stack blocks by controlling the movement of the blocks,",165,110);
        g.drawString("When up and down blocks of the same value are crashed, they will mix,",165,130);
        g.drawString("When the value reaches 64, the block disappears and gains 1 score",165,150);
        g.drawString("When the score reaches 20, you win!",165,170);
        g.drawString("Have a good game!",165,190);
    }

    //Timer listener and keyboard listener
    class TimerListener extends KeyAdapter implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            down();
        }

        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
            	
            	case KeyEvent.VK_LEFT:
            		left();
            		break;
            		
            	case KeyEvent.VK_RIGHT:
                    right();
                    break;
            	
            	case KeyEvent.VK_DOWN:
                    down();
                    break;                            
            }
        }
    }
}

class Block {

    private int value;//Used to store the value of the block
    private int x;//Represent x-axis
    private int y;//Represent y-axis

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }

}