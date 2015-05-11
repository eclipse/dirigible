module Module1
  def self.helloworld(name)
    puts "Hello, #{name}"
    $response.getWriter().println("Hello World!")
  end
end
