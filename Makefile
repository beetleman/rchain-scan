repl:
	docker-compose up -d repl

test-watch:
	docker-compose run --rm tests
