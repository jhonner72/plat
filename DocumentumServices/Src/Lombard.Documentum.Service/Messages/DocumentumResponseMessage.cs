namespace Lombard.Documentum.Service.Messages
{
    // Temporary class until we have better domain
    // Currently used only to distinguish different 
    // queues (by type)

    // TODO: messages will probably be defined at higher level than here

    public class DocumentumResponseMessage
    {
        public string Content { get; set; }

        public DocumentumResponseMessage(string content)
        {
            Content = content;
        }
    }
}
