using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.FileProviders;
using System.Net.NetworkInformation;
var builder = WebApplication.CreateBuilder(args);
var connectionString = builder.Configuration.GetConnectionString("conexionBD") ?? throw new InvalidOperationException("Connection string 'conexionBD' not found.");

builder.Services.AddDbContext<BDContexto>(options => options.UseSqlServer(connectionString));

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
builder.Services.AddOpenApi();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddDirectoryBrowser();

var app = builder.Build();
app.UseStaticFiles(); app.UseStaticFiles(
    new StaticFileOptions
    {
        FileProvider = new PhysicalFileProvider(Path.Combine(
            Directory.GetCurrentDirectory(), "Imagenes")), 
        RequestPath = "/Imagenes"
    });
// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi(); 
    app.UseSwagger(); 
    app.UseSwaggerUI();
}
using (var scope = app.Services.CreateScope()) { 
    var servicio = scope.ServiceProvider;
    var contexto = servicio.GetRequiredService<BDContexto>();
    contexto.Database.EnsureCreated();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
