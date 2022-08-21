package co.edu.unal.tictactoe;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TicTacToeActivity extends Activity {
    // Represents the internal state of the game
    private TicTacToeGame mGame;
    // Buttons making up the board
    private Button mBoardButtons[];
    // Menu Buttons
    private Button mNewGameButton;
    private Button mRestartStatsButton;
    // Various text displayed
    private TextView mInfoTextView;
    private TextView mHumanVictories;
    private TextView mAndroidVictories;
    private TextView mTies;
    // Variables to count results
    private int humanVictories = 0;
    private int androidVictories = 0;
    private int ties = 0;
    // Represents status of the Game
    public boolean mGameOver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);
        mInfoTextView = (TextView) findViewById(R.id.information);
        mHumanVictories = (TextView) findViewById(R.id.Human_Victories);
        mHumanVictories.setText("0");
        mAndroidVictories = (TextView) findViewById(R.id.Android_Victories);
        mAndroidVictories.setText("0");
        mTies = (TextView) findViewById(R.id.Ties);
        mTies.setText("0");
        mNewGameButton= (Button) findViewById(R.id.NewGame);
        mRestartStatsButton = (Button) findViewById(R.id.RestartStats);
        mGame = new TicTacToeGame();
        startNewGame();
    }


    // Set up the game board.
    private void startNewGame() {
        mGame.clearBoard();
        mGameOver = false;
        // Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
        mNewGameButton.setEnabled(true);
        mNewGameButton.setOnClickListener(new ButtonClickListener(9));
        mRestartStatsButton.setEnabled(true);
        mRestartStatsButton.setOnClickListener(new ButtonClickListener(10));
        // Human goes first
        mInfoTextView.setText(R.string.first_human);
    } // End of startNewGame

    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;
        public ButtonClickListener(int location) {
            this.location = location;
        }
        public void onClick(View view) {
            if(location == 9){
                startNewGame();
            }else if (location == 10) {
                humanVictories = 0;
                androidVictories = 0;
                ties = 0;
                startNewGame();
            }else if (mBoardButtons[location].isEnabled()) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);
                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }
                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1){
                    mInfoTextView.setText(R.string.result_tie);
                    ties = ties + 1;
                    mGameOver = true;
                    endGame();
                }else if (winner == 2){
                    mInfoTextView.setText(R.string.result_human_wins);
                    humanVictories = humanVictories + 1;
                    mGameOver = true;
                    endGame();
                }else{
                    mInfoTextView.setText(R.string.result_computer_wins);
                    androidVictories = androidVictories + 1;
                    mGameOver = true;
                    endGame();
                }
            }
            mHumanVictories.setText(Integer.toString(humanVictories));
            mAndroidVictories.setText(Integer.toString(androidVictories));
            mTies.setText(Integer.toString(ties));
        }
    }
    private void setMove(char player, int location) {
        if(!mGameOver){
            mGame.setMove(player, location);
            mBoardButtons[location].setEnabled(false);
            mBoardButtons[location].setText(String.valueOf(player));
            if (player == TicTacToeGame.HUMAN_PLAYER)
                mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
            else
                mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
        }
    }
    private void endGame(){
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setEnabled(false);
        }
    }
}
