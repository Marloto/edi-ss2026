package de.thi.informatik.edi.highscore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import de.thi.informatik.edi.highscore.model.HighScoreEntry;
import reactor.core.publisher.Flux;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/v1/leaderboard")
public class HighScoreController {
	private HighScoreService highScore;

	public HighScoreController(@Autowired HighScoreService highScore) {
		this.highScore = highScore;
	}

	@GetMapping("/{id}")
	public Flux<HighScoreEntry> get(@PathVariable Long id) {
		return this.highScore.get(id).map(HighScoreEntry::new);
	}

	@GetMapping
	public Flux<HighScoreEntry> getAll() {
		return this.highScore.getAll().map(HighScoreEntry::new);
	}
}
