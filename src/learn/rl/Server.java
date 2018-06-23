package learn.rl;

import static spark.Spark.*;

import org.apache.http.entity.ContentType;

public class Server {
	public static void main(String[] args) {
		TicTacToe1 game = new TicTacToe1();
		game.train();
		game.playInit();
		get("/hello", (req, res) -> {
			String pos = req.queryParams("p");
			System.out.println("received:"+pos);
			int p = Integer.parseInt(pos);
			if(p == -1) {
				game.playInit();
				return "game init";
			} else {
				game.humanGo(Integer.parseInt(pos));
				res.header("Access-Control-Allow-Headers", "Content-Type");
				res.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
				res.header("Access-Control-Allow-Origin", "*");
				res.type( ContentType.APPLICATION_JSON.toString() );
				return game.aiGo();
			}
		});
	}
}
