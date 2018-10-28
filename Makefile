rnode:
	docker-compose up -d rchain

bootstrap-test:
	docker-compose exec rchain bash -c "/scripts/generate.sh 10"

test-watch:
	docker-compose run --rm tests

test:
	docker-compose run --rm tests lein test

repl:
	docker-compose up -d repl

start:
	docker-compose run --rm --service-ports repl lein run
